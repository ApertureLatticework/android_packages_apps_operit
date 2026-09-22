package com.ai.assistance.operit.data.db;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.ai.assistance.operit.data.dao.ChatContentDao;
import com.ai.assistance.operit.data.dao.ChatContentDao_Impl;
import com.ai.assistance.operit.data.dao.ChatDao;
import com.ai.assistance.operit.data.dao.ChatDao_Impl;
import com.ai.assistance.operit.data.dao.MessageDao;
import com.ai.assistance.operit.data.dao.MessageDao_Impl;
import com.ai.assistance.operit.data.dao.MessageVariantDao;
import com.ai.assistance.operit.data.dao.MessageVariantDao_Impl;
import com.ai.assistance.operit.data.dao.TokenUsageDao;
import com.ai.assistance.operit.data.dao.TokenUsageDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ChatDao _chatDao;

  private volatile MessageDao _messageDao;

  private volatile MessageVariantDao _messageVariantDao;

  private volatile ChatContentDao _chatContentDao;

  private volatile TokenUsageDao _tokenUsageDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(21, "3eb9b6d4699ec05210dd704ecc46ebcb", "6d5fa50f1599819a63005ffdc646fcad") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `chats` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `inputTokens` INTEGER NOT NULL, `outputTokens` INTEGER NOT NULL, `currentWindowSize` INTEGER NOT NULL, `group` TEXT, `displayOrder` INTEGER NOT NULL, `workspace` TEXT, `workspaceEnv` TEXT, `parentChatId` TEXT, `characterCardName` TEXT, `characterGroupId` TEXT, `locked` INTEGER NOT NULL, `pinned` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `messages` (`messageId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `chatId` TEXT NOT NULL, `sender` TEXT NOT NULL, `content` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `orderIndex` INTEGER NOT NULL, `roleName` TEXT NOT NULL, `selectedVariantIndex` INTEGER NOT NULL, `provider` TEXT NOT NULL, `modelName` TEXT NOT NULL, `inputTokens` INTEGER NOT NULL, `outputTokens` INTEGER NOT NULL, `cachedInputTokens` INTEGER NOT NULL, `sentAt` INTEGER NOT NULL, `outputDurationMs` INTEGER NOT NULL, `waitDurationMs` INTEGER NOT NULL, `completedAt` INTEGER NOT NULL, `displayMode` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, FOREIGN KEY(`chatId`) REFERENCES `chats`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_messages_chatId` ON `messages` (`chatId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_messages_chatId_timestamp` ON `messages` (`chatId`, `timestamp`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `message_variants` (`variantId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `chatId` TEXT NOT NULL, `messageTimestamp` INTEGER NOT NULL, `variantIndex` INTEGER NOT NULL, `content` TEXT NOT NULL, `roleName` TEXT NOT NULL, `provider` TEXT NOT NULL, `modelName` TEXT NOT NULL, `inputTokens` INTEGER NOT NULL, `outputTokens` INTEGER NOT NULL, `cachedInputTokens` INTEGER NOT NULL, `sentAt` INTEGER NOT NULL, `outputDurationMs` INTEGER NOT NULL, `waitDurationMs` INTEGER NOT NULL, `completedAt` INTEGER NOT NULL, FOREIGN KEY(`chatId`) REFERENCES `chats`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_message_variants_chatId_messageTimestamp` ON `message_variants` (`chatId`, `messageTimestamp`)");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_message_variants_chatId_messageTimestamp_variantIndex` ON `message_variants` (`chatId`, `messageTimestamp`, `variantIndex`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `token_usage_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `importKey` TEXT, `occurredAtMs` INTEGER, `configId` TEXT NOT NULL, `provider` TEXT NOT NULL, `model` TEXT NOT NULL, `requestCount` INTEGER NOT NULL, `uncachedInputTokens` INTEGER, `cachedInputTokens` INTEGER, `cacheWriteTokens` INTEGER, `totalInputTokens` INTEGER, `outputTokens` INTEGER)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_token_usage_records_occurredAtMs` ON `token_usage_records` (`occurredAtMs`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_token_usage_records_provider_model_configId_occurredAtMs` ON `token_usage_records` (`provider`, `model`, `configId`, `occurredAtMs`)");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_token_usage_records_importKey` ON `token_usage_records` (`importKey`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `token_stats_models` (`configId` TEXT NOT NULL, `provider` TEXT NOT NULL, `model` TEXT NOT NULL, `billingMode` TEXT, `currency` TEXT, `inputPricePerMillion` REAL, `cachedInputPricePerMillion` REAL, `cacheWritePricePerMillion` REAL, `outputPricePerMillion` REAL, `pricePerRequest` REAL, PRIMARY KEY(`configId`, `provider`, `model`))");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '3eb9b6d4699ec05210dd704ecc46ebcb')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `chats`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `messages`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `message_variants`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `token_usage_records`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `token_stats_models`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsChats = new HashMap<String, TableInfo.Column>(16);
        _columnsChats.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("inputTokens", new TableInfo.Column("inputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("outputTokens", new TableInfo.Column("outputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("currentWindowSize", new TableInfo.Column("currentWindowSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("group", new TableInfo.Column("group", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("displayOrder", new TableInfo.Column("displayOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("workspace", new TableInfo.Column("workspace", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("workspaceEnv", new TableInfo.Column("workspaceEnv", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("parentChatId", new TableInfo.Column("parentChatId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("characterCardName", new TableInfo.Column("characterCardName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("characterGroupId", new TableInfo.Column("characterGroupId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("locked", new TableInfo.Column("locked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChats.put("pinned", new TableInfo.Column("pinned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysChats = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesChats = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChats = new TableInfo("chats", _columnsChats, _foreignKeysChats, _indicesChats);
        final TableInfo _existingChats = TableInfo.read(connection, "chats");
        if (!_infoChats.equals(_existingChats)) {
          return new RoomOpenDelegate.ValidationResult(false, "chats(com.ai.assistance.operit.data.model.ChatEntity).\n"
                  + " Expected:\n" + _infoChats + "\n"
                  + " Found:\n" + _existingChats);
        }
        final Map<String, TableInfo.Column> _columnsMessages = new HashMap<String, TableInfo.Column>(19);
        _columnsMessages.put("messageId", new TableInfo.Column("messageId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("chatId", new TableInfo.Column("chatId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("sender", new TableInfo.Column("sender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("roleName", new TableInfo.Column("roleName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("selectedVariantIndex", new TableInfo.Column("selectedVariantIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("provider", new TableInfo.Column("provider", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("modelName", new TableInfo.Column("modelName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("inputTokens", new TableInfo.Column("inputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("outputTokens", new TableInfo.Column("outputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("cachedInputTokens", new TableInfo.Column("cachedInputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("sentAt", new TableInfo.Column("sentAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("outputDurationMs", new TableInfo.Column("outputDurationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("waitDurationMs", new TableInfo.Column("waitDurationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("displayMode", new TableInfo.Column("displayMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMessages = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMessages.add(new TableInfo.ForeignKey("chats", "CASCADE", "NO ACTION", Arrays.asList("chatId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesMessages = new HashSet<TableInfo.Index>(2);
        _indicesMessages.add(new TableInfo.Index("index_messages_chatId", false, Arrays.asList("chatId"), Arrays.asList("ASC")));
        _indicesMessages.add(new TableInfo.Index("index_messages_chatId_timestamp", false, Arrays.asList("chatId", "timestamp"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoMessages = new TableInfo("messages", _columnsMessages, _foreignKeysMessages, _indicesMessages);
        final TableInfo _existingMessages = TableInfo.read(connection, "messages");
        if (!_infoMessages.equals(_existingMessages)) {
          return new RoomOpenDelegate.ValidationResult(false, "messages(com.ai.assistance.operit.data.model.MessageEntity).\n"
                  + " Expected:\n" + _infoMessages + "\n"
                  + " Found:\n" + _existingMessages);
        }
        final Map<String, TableInfo.Column> _columnsMessageVariants = new HashMap<String, TableInfo.Column>(15);
        _columnsMessageVariants.put("variantId", new TableInfo.Column("variantId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("chatId", new TableInfo.Column("chatId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("messageTimestamp", new TableInfo.Column("messageTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("variantIndex", new TableInfo.Column("variantIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("roleName", new TableInfo.Column("roleName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("provider", new TableInfo.Column("provider", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("modelName", new TableInfo.Column("modelName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("inputTokens", new TableInfo.Column("inputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("outputTokens", new TableInfo.Column("outputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("cachedInputTokens", new TableInfo.Column("cachedInputTokens", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("sentAt", new TableInfo.Column("sentAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("outputDurationMs", new TableInfo.Column("outputDurationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("waitDurationMs", new TableInfo.Column("waitDurationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageVariants.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMessageVariants = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMessageVariants.add(new TableInfo.ForeignKey("chats", "CASCADE", "NO ACTION", Arrays.asList("chatId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesMessageVariants = new HashSet<TableInfo.Index>(2);
        _indicesMessageVariants.add(new TableInfo.Index("index_message_variants_chatId_messageTimestamp", false, Arrays.asList("chatId", "messageTimestamp"), Arrays.asList("ASC", "ASC")));
        _indicesMessageVariants.add(new TableInfo.Index("index_message_variants_chatId_messageTimestamp_variantIndex", true, Arrays.asList("chatId", "messageTimestamp", "variantIndex"), Arrays.asList("ASC", "ASC", "ASC")));
        final TableInfo _infoMessageVariants = new TableInfo("message_variants", _columnsMessageVariants, _foreignKeysMessageVariants, _indicesMessageVariants);
        final TableInfo _existingMessageVariants = TableInfo.read(connection, "message_variants");
        if (!_infoMessageVariants.equals(_existingMessageVariants)) {
          return new RoomOpenDelegate.ValidationResult(false, "message_variants(com.ai.assistance.operit.data.model.MessageVariantEntity).\n"
                  + " Expected:\n" + _infoMessageVariants + "\n"
                  + " Found:\n" + _existingMessageVariants);
        }
        final Map<String, TableInfo.Column> _columnsTokenUsageRecords = new HashMap<String, TableInfo.Column>(12);
        _columnsTokenUsageRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("importKey", new TableInfo.Column("importKey", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("occurredAtMs", new TableInfo.Column("occurredAtMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("configId", new TableInfo.Column("configId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("provider", new TableInfo.Column("provider", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("model", new TableInfo.Column("model", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("requestCount", new TableInfo.Column("requestCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("uncachedInputTokens", new TableInfo.Column("uncachedInputTokens", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("cachedInputTokens", new TableInfo.Column("cachedInputTokens", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("cacheWriteTokens", new TableInfo.Column("cacheWriteTokens", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("totalInputTokens", new TableInfo.Column("totalInputTokens", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenUsageRecords.put("outputTokens", new TableInfo.Column("outputTokens", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTokenUsageRecords = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesTokenUsageRecords = new HashSet<TableInfo.Index>(3);
        _indicesTokenUsageRecords.add(new TableInfo.Index("index_token_usage_records_occurredAtMs", false, Arrays.asList("occurredAtMs"), Arrays.asList("ASC")));
        _indicesTokenUsageRecords.add(new TableInfo.Index("index_token_usage_records_provider_model_configId_occurredAtMs", false, Arrays.asList("provider", "model", "configId", "occurredAtMs"), Arrays.asList("ASC", "ASC", "ASC", "ASC")));
        _indicesTokenUsageRecords.add(new TableInfo.Index("index_token_usage_records_importKey", true, Arrays.asList("importKey"), Arrays.asList("ASC")));
        final TableInfo _infoTokenUsageRecords = new TableInfo("token_usage_records", _columnsTokenUsageRecords, _foreignKeysTokenUsageRecords, _indicesTokenUsageRecords);
        final TableInfo _existingTokenUsageRecords = TableInfo.read(connection, "token_usage_records");
        if (!_infoTokenUsageRecords.equals(_existingTokenUsageRecords)) {
          return new RoomOpenDelegate.ValidationResult(false, "token_usage_records(com.ai.assistance.operit.data.model.TokenUsageRecordEntity).\n"
                  + " Expected:\n" + _infoTokenUsageRecords + "\n"
                  + " Found:\n" + _existingTokenUsageRecords);
        }
        final Map<String, TableInfo.Column> _columnsTokenStatsModels = new HashMap<String, TableInfo.Column>(10);
        _columnsTokenStatsModels.put("configId", new TableInfo.Column("configId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("provider", new TableInfo.Column("provider", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("model", new TableInfo.Column("model", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("billingMode", new TableInfo.Column("billingMode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("currency", new TableInfo.Column("currency", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("inputPricePerMillion", new TableInfo.Column("inputPricePerMillion", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("cachedInputPricePerMillion", new TableInfo.Column("cachedInputPricePerMillion", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("cacheWritePricePerMillion", new TableInfo.Column("cacheWritePricePerMillion", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("outputPricePerMillion", new TableInfo.Column("outputPricePerMillion", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTokenStatsModels.put("pricePerRequest", new TableInfo.Column("pricePerRequest", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTokenStatsModels = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesTokenStatsModels = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTokenStatsModels = new TableInfo("token_stats_models", _columnsTokenStatsModels, _foreignKeysTokenStatsModels, _indicesTokenStatsModels);
        final TableInfo _existingTokenStatsModels = TableInfo.read(connection, "token_stats_models");
        if (!_infoTokenStatsModels.equals(_existingTokenStatsModels)) {
          return new RoomOpenDelegate.ValidationResult(false, "token_stats_models(com.ai.assistance.operit.data.model.TokenStatsModelEntity).\n"
                  + " Expected:\n" + _infoTokenStatsModels + "\n"
                  + " Found:\n" + _existingTokenStatsModels);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "chats", "messages", "message_variants", "token_usage_records", "token_stats_models");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "chats", "messages", "message_variants", "token_usage_records", "token_stats_models");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ChatDao.class, ChatDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MessageDao.class, MessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MessageVariantDao.class, MessageVariantDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatContentDao.class, ChatContentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TokenUsageDao.class, TokenUsageDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ChatDao chatDao() {
    if (_chatDao != null) {
      return _chatDao;
    } else {
      synchronized(this) {
        if(_chatDao == null) {
          _chatDao = new ChatDao_Impl(this);
        }
        return _chatDao;
      }
    }
  }

  @Override
  public MessageDao messageDao() {
    if (_messageDao != null) {
      return _messageDao;
    } else {
      synchronized(this) {
        if(_messageDao == null) {
          _messageDao = new MessageDao_Impl(this);
        }
        return _messageDao;
      }
    }
  }

  @Override
  public MessageVariantDao messageVariantDao() {
    if (_messageVariantDao != null) {
      return _messageVariantDao;
    } else {
      synchronized(this) {
        if(_messageVariantDao == null) {
          _messageVariantDao = new MessageVariantDao_Impl(this);
        }
        return _messageVariantDao;
      }
    }
  }

  @Override
  public ChatContentDao chatContentDao() {
    if (_chatContentDao != null) {
      return _chatContentDao;
    } else {
      synchronized(this) {
        if(_chatContentDao == null) {
          _chatContentDao = new ChatContentDao_Impl(this);
        }
        return _chatContentDao;
      }
    }
  }

  @Override
  public TokenUsageDao tokenUsageDao() {
    if (_tokenUsageDao != null) {
      return _tokenUsageDao;
    } else {
      synchronized(this) {
        if(_tokenUsageDao == null) {
          _tokenUsageDao = new TokenUsageDao_Impl(this);
        }
        return _tokenUsageDao;
      }
    }
  }
}
