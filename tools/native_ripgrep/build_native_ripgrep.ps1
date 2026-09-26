param(
    [string[]]$Targets = @("aarch64-linux-android"),
    [string]$SdkDir = "",
    [int]$ApiLevel = 31
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$crateDir = $PSScriptRoot
$localPropertiesPath = Join-Path $repoRoot "local.properties"

if ([string]::IsNullOrWhiteSpace($SdkDir)) {
    if (Test-Path $localPropertiesPath) {
        $sdkLine = Get-Content $localPropertiesPath | Where-Object { $_ -match '^sdk\.dir=' } | Select-Object -First 1
        if ($sdkLine) {
            $SdkDir = ($sdkLine -replace '^sdk\.dir=', '') -replace '\\:', ':'
        }
    }
}

if ([string]::IsNullOrWhiteSpace($SdkDir)) {
    $SdkDir = $env:ANDROID_HOME
}
if ([string]::IsNullOrWhiteSpace($SdkDir)) {
    $SdkDir = $env:ANDROID_SDK_ROOT
}
if ([string]::IsNullOrWhiteSpace($SdkDir)) {
    throw "Android SDK path was not found"
}

$ndkRoot = $env:ANDROID_NDK_HOME
if ([string]::IsNullOrWhiteSpace($ndkRoot)) {
    $ndkRoot = $env:ANDROID_NDK_ROOT
}
if ([string]::IsNullOrWhiteSpace($ndkRoot)) {
    $ndkDir = Join-Path $SdkDir "ndk"
    $ndkRoot = Get-ChildItem $ndkDir -Directory |
        Sort-Object { [version]$_.Name } -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
if ([string]::IsNullOrWhiteSpace($ndkRoot)) {
    throw "Android NDK path was not found"
}

$toolchainBin = Join-Path $ndkRoot "toolchains\llvm\prebuilt\windows-x86_64\bin"
# 单 ABI 是有意设计：jniLibs 与 ffmpeg jni 均只发 arm64-v8a（与三份 workflow 的
# NDK linker driver 预检步同源声明）；构建其它 triple 只会产出无 ffmpeg 伙伴库的
# 残废 jniLibs。未来扩多 ABI 时，在此表补 triple→Linker/Abi 条目（旧四条目见
# git 历史），并同步扩 CI 侧预检清单。
$targetConfig = @{
    "aarch64-linux-android" = @{ Linker = "aarch64-linux-android$ApiLevel-clang.cmd"; Abi = "arm64-v8a" }
}

foreach ($target in $Targets) {
    if (-not $targetConfig.ContainsKey($target)) {
        throw "Unsupported target: $target"
    }
    rustup target add $target | Out-Host
    $config = $targetConfig[$target]
    $linker = Join-Path $toolchainBin $config.Linker
    if (-not (Test-Path $linker)) {
        throw "Android linker was not found: $linker"
    }

    $envName = "CARGO_TARGET_$($target.ToUpperInvariant().Replace('-', '_'))_LINKER"
    Set-Item -Path "Env:$envName" -Value $linker
    cargo build --manifest-path (Join-Path $crateDir "Cargo.toml") --release --target $target | Out-Host

    $source = Join-Path $crateDir "target\$target\release\liboperit_ripgrep.so"
    $destinationDir = Join-Path $repoRoot "app\src\main\jniLibs\$($config.Abi)"
    New-Item -ItemType Directory -Force -Path $destinationDir | Out-Null
    Copy-Item -LiteralPath $source -Destination (Join-Path $destinationDir "liboperit_ripgrep.so") -Force
    Write-Host "Built $target -> $destinationDir\liboperit_ripgrep.so"
}
