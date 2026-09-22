package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteConnectionUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.TokenStatsModelEntity;
import com.ai.assistance.operit.data.model.TokenUsageRecordEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class TokenUsageDao_Impl extends TokenUsageDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<TokenUsageRecordEntity> __insertAdapterOfTokenUsageRecordEntity;

  private final EntityInsertAdapter<TokenStatsModelEntity> __insertAdapterOfTokenStatsModelEntity;

  public TokenUsageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTokenUsageRecordEntity = new EntityInsertAdapter<TokenUsageRecordEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `token_usage_records` (`id`,`importKey`,`occurredAtMs`,`configId`,`provider`,`model`,`requestCount`,`uncachedInputTokens`,`cachedInputTokens`,`cacheWriteTokens`,`totalInputTokens`,`outputTokens`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TokenUsageRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getImportKey() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getImportKey());
        }
        if (entity.getOccurredAtMs() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getOccurredAtMs());
        }
        if (entity.getConfigId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getConfigId());
        }
        if (entity.getProvider() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getProvider());
        }
        if (entity.getModel() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getModel());
        }
        statement.bindLong(7, entity.getRequestCount());
        if (entity.getUncachedInputTokens() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getUncachedInputTokens());
        }
        if (entity.getCachedInputTokens() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getCachedInputTokens());
        }
        if (entity.getCacheWriteTokens() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getCacheWriteTokens());
        }
        if (entity.getTotalInputTokens() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getTotalInputTokens());
        }
        if (entity.getOutputTokens() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getOutputTokens());
        }
      }
    };
    this.__insertAdapterOfTokenStatsModelEntity = new EntityInsertAdapter<TokenStatsModelEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `token_stats_models` (`configId`,`provider`,`model`,`billingMode`,`currency`,`inputPricePerMillion`,`cachedInputPricePerMillion`,`cacheWritePricePerMillion`,`outputPricePerMillion`,`pricePerRequest`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TokenStatsModelEntity entity) {
        if (entity.getConfigId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getConfigId());
        }
        if (entity.getProvider() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getProvider());
        }
        if (entity.getModel() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getModel());
        }
        if (entity.getBillingMode() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getBillingMode());
        }
        if (entity.getCurrency() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getCurrency());
        }
        if (entity.getInputPricePerMillion() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getInputPricePerMillion());
        }
        if (entity.getCachedInputPricePerMillion() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getCachedInputPricePerMillion());
        }
        if (entity.getCacheWritePricePerMillion() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getCacheWritePricePerMillion());
        }
        if (entity.getOutputPricePerMillion() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getOutputPricePerMillion());
        }
        if (entity.getPricePerRequest() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getPricePerRequest());
        }
      }
    };
  }

  @Override
  public Object insertRecord(final TokenUsageRecordEntity record,
      final Continuation<? super Long> $completion) {
    if (record == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfTokenUsageRecordEntity.insertAndReturnId(_connection, record);
    }, $completion);
  }

  @Override
  public Object insertRecords(final List<TokenUsageRecordEntity> records,
      final Continuation<? super Unit> $completion) {
    if (records == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfTokenUsageRecordEntity.insert(_connection, records);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object upsertStatsModel(final TokenStatsModelEntity model,
      final Continuation<? super Unit> $completion) {
    if (model == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfTokenStatsModelEntity.insert(_connection, model);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getStatsModel(final String configId, final String provider, final String model,
      final Continuation<? super TokenStatsModelEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM token_stats_models\n"
            + "        WHERE configId = ? AND provider = ? AND model = ?\n"
            + "        ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (configId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, configId);
        }
        _argIndex = 2;
        if (provider == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, provider);
        }
        _argIndex = 3;
        if (model == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, model);
        }
        final int _columnIndexOfConfigId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "configId");
        final int _columnIndexOfProvider = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "provider");
        final int _columnIndexOfModel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "model");
        final int _columnIndexOfBillingMode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "billingMode");
        final int _columnIndexOfCurrency = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currency");
        final int _columnIndexOfInputPricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputPricePerMillion");
        final int _columnIndexOfCachedInputPricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cachedInputPricePerMillion");
        final int _columnIndexOfCacheWritePricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cacheWritePricePerMillion");
        final int _columnIndexOfOutputPricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputPricePerMillion");
        final int _columnIndexOfPricePerRequest = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pricePerRequest");
        final TokenStatsModelEntity _result;
        if (_stmt.step()) {
          final String _tmpConfigId;
          if (_stmt.isNull(_columnIndexOfConfigId)) {
            _tmpConfigId = null;
          } else {
            _tmpConfigId = _stmt.getText(_columnIndexOfConfigId);
          }
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModel;
          if (_stmt.isNull(_columnIndexOfModel)) {
            _tmpModel = null;
          } else {
            _tmpModel = _stmt.getText(_columnIndexOfModel);
          }
          final String _tmpBillingMode;
          if (_stmt.isNull(_columnIndexOfBillingMode)) {
            _tmpBillingMode = null;
          } else {
            _tmpBillingMode = _stmt.getText(_columnIndexOfBillingMode);
          }
          final String _tmpCurrency;
          if (_stmt.isNull(_columnIndexOfCurrency)) {
            _tmpCurrency = null;
          } else {
            _tmpCurrency = _stmt.getText(_columnIndexOfCurrency);
          }
          final Double _tmpInputPricePerMillion;
          if (_stmt.isNull(_columnIndexOfInputPricePerMillion)) {
            _tmpInputPricePerMillion = null;
          } else {
            _tmpInputPricePerMillion = _stmt.getDouble(_columnIndexOfInputPricePerMillion);
          }
          final Double _tmpCachedInputPricePerMillion;
          if (_stmt.isNull(_columnIndexOfCachedInputPricePerMillion)) {
            _tmpCachedInputPricePerMillion = null;
          } else {
            _tmpCachedInputPricePerMillion = _stmt.getDouble(_columnIndexOfCachedInputPricePerMillion);
          }
          final Double _tmpCacheWritePricePerMillion;
          if (_stmt.isNull(_columnIndexOfCacheWritePricePerMillion)) {
            _tmpCacheWritePricePerMillion = null;
          } else {
            _tmpCacheWritePricePerMillion = _stmt.getDouble(_columnIndexOfCacheWritePricePerMillion);
          }
          final Double _tmpOutputPricePerMillion;
          if (_stmt.isNull(_columnIndexOfOutputPricePerMillion)) {
            _tmpOutputPricePerMillion = null;
          } else {
            _tmpOutputPricePerMillion = _stmt.getDouble(_columnIndexOfOutputPricePerMillion);
          }
          final Double _tmpPricePerRequest;
          if (_stmt.isNull(_columnIndexOfPricePerRequest)) {
            _tmpPricePerRequest = null;
          } else {
            _tmpPricePerRequest = _stmt.getDouble(_columnIndexOfPricePerRequest);
          }
          _result = new TokenStatsModelEntity(_tmpConfigId,_tmpProvider,_tmpModel,_tmpBillingMode,_tmpCurrency,_tmpInputPricePerMillion,_tmpCachedInputPricePerMillion,_tmpCacheWritePricePerMillion,_tmpOutputPricePerMillion,_tmpPricePerRequest);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAllStatsModels(
      final Continuation<? super List<TokenStatsModelEntity>> $completion) {
    final String _sql = "SELECT * FROM token_stats_models ORDER BY provider, model, configId";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfConfigId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "configId");
        final int _columnIndexOfProvider = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "provider");
        final int _columnIndexOfModel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "model");
        final int _columnIndexOfBillingMode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "billingMode");
        final int _columnIndexOfCurrency = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currency");
        final int _columnIndexOfInputPricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputPricePerMillion");
        final int _columnIndexOfCachedInputPricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cachedInputPricePerMillion");
        final int _columnIndexOfCacheWritePricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cacheWritePricePerMillion");
        final int _columnIndexOfOutputPricePerMillion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputPricePerMillion");
        final int _columnIndexOfPricePerRequest = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pricePerRequest");
        final List<TokenStatsModelEntity> _result = new ArrayList<TokenStatsModelEntity>();
        while (_stmt.step()) {
          final TokenStatsModelEntity _item;
          final String _tmpConfigId;
          if (_stmt.isNull(_columnIndexOfConfigId)) {
            _tmpConfigId = null;
          } else {
            _tmpConfigId = _stmt.getText(_columnIndexOfConfigId);
          }
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModel;
          if (_stmt.isNull(_columnIndexOfModel)) {
            _tmpModel = null;
          } else {
            _tmpModel = _stmt.getText(_columnIndexOfModel);
          }
          final String _tmpBillingMode;
          if (_stmt.isNull(_columnIndexOfBillingMode)) {
            _tmpBillingMode = null;
          } else {
            _tmpBillingMode = _stmt.getText(_columnIndexOfBillingMode);
          }
          final String _tmpCurrency;
          if (_stmt.isNull(_columnIndexOfCurrency)) {
            _tmpCurrency = null;
          } else {
            _tmpCurrency = _stmt.getText(_columnIndexOfCurrency);
          }
          final Double _tmpInputPricePerMillion;
          if (_stmt.isNull(_columnIndexOfInputPricePerMillion)) {
            _tmpInputPricePerMillion = null;
          } else {
            _tmpInputPricePerMillion = _stmt.getDouble(_columnIndexOfInputPricePerMillion);
          }
          final Double _tmpCachedInputPricePerMillion;
          if (_stmt.isNull(_columnIndexOfCachedInputPricePerMillion)) {
            _tmpCachedInputPricePerMillion = null;
          } else {
            _tmpCachedInputPricePerMillion = _stmt.getDouble(_columnIndexOfCachedInputPricePerMillion);
          }
          final Double _tmpCacheWritePricePerMillion;
          if (_stmt.isNull(_columnIndexOfCacheWritePricePerMillion)) {
            _tmpCacheWritePricePerMillion = null;
          } else {
            _tmpCacheWritePricePerMillion = _stmt.getDouble(_columnIndexOfCacheWritePricePerMillion);
          }
          final Double _tmpOutputPricePerMillion;
          if (_stmt.isNull(_columnIndexOfOutputPricePerMillion)) {
            _tmpOutputPricePerMillion = null;
          } else {
            _tmpOutputPricePerMillion = _stmt.getDouble(_columnIndexOfOutputPricePerMillion);
          }
          final Double _tmpPricePerRequest;
          if (_stmt.isNull(_columnIndexOfPricePerRequest)) {
            _tmpPricePerRequest = null;
          } else {
            _tmpPricePerRequest = _stmt.getDouble(_columnIndexOfPricePerRequest);
          }
          _item = new TokenStatsModelEntity(_tmpConfigId,_tmpProvider,_tmpModel,_tmpBillingMode,_tmpCurrency,_tmpInputPricePerMillion,_tmpCachedInputPricePerMillion,_tmpCacheWritePricePerMillion,_tmpOutputPricePerMillion,_tmpPricePerRequest);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object aggregateModelsForLifetime(final List<String> providerModels,
      final boolean allModels,
      final Continuation<? super List<TokenUsageModelAggregateRow>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT");
    _stringBuilder.append("\n");
    _stringBuilder.append("            provider AS provider,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            model AS model,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            configId AS configId,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(requestCount), 0) AS requests,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(uncachedInputTokens), 0) AS uncachedInputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN uncachedInputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS uncachedInputKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(cachedInputTokens), 0) AS cachedInputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN cachedInputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS cachedInputKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(cacheWriteTokens), 0) AS cacheWriteTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN cacheWriteTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS cacheWriteKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(totalInputTokens), 0) AS totalInputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN totalInputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS totalInputKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(outputTokens), 0) AS outputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN outputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS outputKnown");
    _stringBuilder.append("\n");
    _stringBuilder.append("        FROM token_usage_records");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE (");
    _stringBuilder.append("?");
    _stringBuilder.append(" OR (provider || ':' || model) IN (");
    final int _inputSize = providerModels.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("        GROUP BY provider, model, configId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY provider, model, configId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = allModels ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        for (String _item : providerModels) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfProvider = 0;
        final int _columnIndexOfModel = 1;
        final int _columnIndexOfConfigId = 2;
        final int _columnIndexOfRequests = 3;
        final int _columnIndexOfUncachedInputTokens = 4;
        final int _columnIndexOfUncachedInputKnown = 5;
        final int _columnIndexOfCachedInputTokens = 6;
        final int _columnIndexOfCachedInputKnown = 7;
        final int _columnIndexOfCacheWriteTokens = 8;
        final int _columnIndexOfCacheWriteKnown = 9;
        final int _columnIndexOfTotalInputTokens = 10;
        final int _columnIndexOfTotalInputKnown = 11;
        final int _columnIndexOfOutputTokens = 12;
        final int _columnIndexOfOutputKnown = 13;
        final List<TokenUsageModelAggregateRow> _result = new ArrayList<TokenUsageModelAggregateRow>();
        while (_stmt.step()) {
          final TokenUsageModelAggregateRow _item_1;
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModel;
          if (_stmt.isNull(_columnIndexOfModel)) {
            _tmpModel = null;
          } else {
            _tmpModel = _stmt.getText(_columnIndexOfModel);
          }
          final String _tmpConfigId;
          if (_stmt.isNull(_columnIndexOfConfigId)) {
            _tmpConfigId = null;
          } else {
            _tmpConfigId = _stmt.getText(_columnIndexOfConfigId);
          }
          final long _tmpRequests;
          _tmpRequests = _stmt.getLong(_columnIndexOfRequests);
          final long _tmpUncachedInputTokens;
          _tmpUncachedInputTokens = _stmt.getLong(_columnIndexOfUncachedInputTokens);
          final long _tmpUncachedInputKnown;
          _tmpUncachedInputKnown = _stmt.getLong(_columnIndexOfUncachedInputKnown);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpCachedInputKnown;
          _tmpCachedInputKnown = _stmt.getLong(_columnIndexOfCachedInputKnown);
          final long _tmpCacheWriteTokens;
          _tmpCacheWriteTokens = _stmt.getLong(_columnIndexOfCacheWriteTokens);
          final long _tmpCacheWriteKnown;
          _tmpCacheWriteKnown = _stmt.getLong(_columnIndexOfCacheWriteKnown);
          final long _tmpTotalInputTokens;
          _tmpTotalInputTokens = _stmt.getLong(_columnIndexOfTotalInputTokens);
          final long _tmpTotalInputKnown;
          _tmpTotalInputKnown = _stmt.getLong(_columnIndexOfTotalInputKnown);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpOutputKnown;
          _tmpOutputKnown = _stmt.getLong(_columnIndexOfOutputKnown);
          _item_1 = new TokenUsageModelAggregateRow(_tmpProvider,_tmpModel,_tmpConfigId,_tmpRequests,_tmpUncachedInputTokens,_tmpUncachedInputKnown,_tmpCachedInputTokens,_tmpCachedInputKnown,_tmpCacheWriteTokens,_tmpCacheWriteKnown,_tmpTotalInputTokens,_tmpTotalInputKnown,_tmpOutputTokens,_tmpOutputKnown);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getEarliestOccurredAtMs(final List<String> providerModels, final boolean allModels,
      final Continuation<? super Long> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT MIN(occurredAtMs)");
    _stringBuilder.append("\n");
    _stringBuilder.append("        FROM token_usage_records");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE occurredAtMs IS NOT NULL");
    _stringBuilder.append("\n");
    _stringBuilder.append("            AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" OR (provider || ':' || model) IN (");
    final int _inputSize = providerModels.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = allModels ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        for (String _item : providerModels) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        final Long _result;
        if (_stmt.step()) {
          final Long _tmp_1;
          if (_stmt.isNull(0)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(0);
          }
          _result = _tmp_1;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object aggregateModelsInRange(final long startMs, final long endMs,
      final List<String> providerModels, final boolean allModels,
      final Continuation<? super List<TokenUsageModelAggregateRow>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT");
    _stringBuilder.append("\n");
    _stringBuilder.append("            provider AS provider,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            model AS model,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            configId AS configId,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(requestCount), 0) AS requests,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(uncachedInputTokens), 0) AS uncachedInputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN uncachedInputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS uncachedInputKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(cachedInputTokens), 0) AS cachedInputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN cachedInputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS cachedInputKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(cacheWriteTokens), 0) AS cacheWriteTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN cacheWriteTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS cacheWriteKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(totalInputTokens), 0) AS totalInputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN totalInputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS totalInputKnown,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(outputTokens), 0) AS outputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN outputTokens IS NOT NULL THEN requestCount ELSE 0 END), 0) AS outputKnown");
    _stringBuilder.append("\n");
    _stringBuilder.append("        FROM token_usage_records");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE occurredAtMs >= ");
    _stringBuilder.append("?");
    _stringBuilder.append(" AND occurredAtMs < ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("            AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" OR (provider || ':' || model) IN (");
    final int _inputSize = providerModels.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("        GROUP BY provider, model, configId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY provider, model, configId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startMs);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, endMs);
        _argIndex = 3;
        final int _tmp = allModels ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 4;
        for (String _item : providerModels) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfProvider = 0;
        final int _columnIndexOfModel = 1;
        final int _columnIndexOfConfigId = 2;
        final int _columnIndexOfRequests = 3;
        final int _columnIndexOfUncachedInputTokens = 4;
        final int _columnIndexOfUncachedInputKnown = 5;
        final int _columnIndexOfCachedInputTokens = 6;
        final int _columnIndexOfCachedInputKnown = 7;
        final int _columnIndexOfCacheWriteTokens = 8;
        final int _columnIndexOfCacheWriteKnown = 9;
        final int _columnIndexOfTotalInputTokens = 10;
        final int _columnIndexOfTotalInputKnown = 11;
        final int _columnIndexOfOutputTokens = 12;
        final int _columnIndexOfOutputKnown = 13;
        final List<TokenUsageModelAggregateRow> _result = new ArrayList<TokenUsageModelAggregateRow>();
        while (_stmt.step()) {
          final TokenUsageModelAggregateRow _item_1;
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModel;
          if (_stmt.isNull(_columnIndexOfModel)) {
            _tmpModel = null;
          } else {
            _tmpModel = _stmt.getText(_columnIndexOfModel);
          }
          final String _tmpConfigId;
          if (_stmt.isNull(_columnIndexOfConfigId)) {
            _tmpConfigId = null;
          } else {
            _tmpConfigId = _stmt.getText(_columnIndexOfConfigId);
          }
          final long _tmpRequests;
          _tmpRequests = _stmt.getLong(_columnIndexOfRequests);
          final long _tmpUncachedInputTokens;
          _tmpUncachedInputTokens = _stmt.getLong(_columnIndexOfUncachedInputTokens);
          final long _tmpUncachedInputKnown;
          _tmpUncachedInputKnown = _stmt.getLong(_columnIndexOfUncachedInputKnown);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpCachedInputKnown;
          _tmpCachedInputKnown = _stmt.getLong(_columnIndexOfCachedInputKnown);
          final long _tmpCacheWriteTokens;
          _tmpCacheWriteTokens = _stmt.getLong(_columnIndexOfCacheWriteTokens);
          final long _tmpCacheWriteKnown;
          _tmpCacheWriteKnown = _stmt.getLong(_columnIndexOfCacheWriteKnown);
          final long _tmpTotalInputTokens;
          _tmpTotalInputTokens = _stmt.getLong(_columnIndexOfTotalInputTokens);
          final long _tmpTotalInputKnown;
          _tmpTotalInputKnown = _stmt.getLong(_columnIndexOfTotalInputKnown);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpOutputKnown;
          _tmpOutputKnown = _stmt.getLong(_columnIndexOfOutputKnown);
          _item_1 = new TokenUsageModelAggregateRow(_tmpProvider,_tmpModel,_tmpConfigId,_tmpRequests,_tmpUncachedInputTokens,_tmpUncachedInputKnown,_tmpCachedInputTokens,_tmpCachedInputKnown,_tmpCacheWriteTokens,_tmpCacheWriteKnown,_tmpTotalInputTokens,_tmpTotalInputKnown,_tmpOutputTokens,_tmpOutputKnown);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getActivityDaysInRange(final long startMs, final long endMs,
      final List<String> providerModels, final boolean allModels,
      final Continuation<? super List<TokenUsageActivityDayRow>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT");
    _stringBuilder.append("\n");
    _stringBuilder.append("            strftime('%Y-%m-%d', occurredAtMs / 1000, 'unixepoch', 'localtime') AS localDate,");
    _stringBuilder.append("\n");
    _stringBuilder.append("            COALESCE(SUM(");
    _stringBuilder.append("\n");
    _stringBuilder.append("                COALESCE(");
    _stringBuilder.append("\n");
    _stringBuilder.append("                    totalInputTokens,");
    _stringBuilder.append("\n");
    _stringBuilder.append("                    CASE");
    _stringBuilder.append("\n");
    _stringBuilder.append("                        WHEN uncachedInputTokens IS NOT NULL");
    _stringBuilder.append("\n");
    _stringBuilder.append("                            AND cachedInputTokens IS NOT NULL");
    _stringBuilder.append("\n");
    _stringBuilder.append("                            AND cacheWriteTokens IS NOT NULL");
    _stringBuilder.append("\n");
    _stringBuilder.append("                        THEN uncachedInputTokens + cachedInputTokens + cacheWriteTokens");
    _stringBuilder.append("\n");
    _stringBuilder.append("                    END,");
    _stringBuilder.append("\n");
    _stringBuilder.append("                    0");
    _stringBuilder.append("\n");
    _stringBuilder.append("                ) + COALESCE(outputTokens, 0)");
    _stringBuilder.append("\n");
    _stringBuilder.append("            ), 0) AS tokens");
    _stringBuilder.append("\n");
    _stringBuilder.append("        FROM token_usage_records");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE occurredAtMs >= ");
    _stringBuilder.append("?");
    _stringBuilder.append(" AND occurredAtMs < ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("            AND (");
    _stringBuilder.append("?");
    _stringBuilder.append(" OR (provider || ':' || model) IN (");
    final int _inputSize = providerModels.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("        GROUP BY localDate, configId, provider, model");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY localDate, provider, model, configId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startMs);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, endMs);
        _argIndex = 3;
        final int _tmp = allModels ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 4;
        for (String _item : providerModels) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfLocalDate = 0;
        final int _columnIndexOfTokens = 1;
        final List<TokenUsageActivityDayRow> _result = new ArrayList<TokenUsageActivityDayRow>();
        while (_stmt.step()) {
          final TokenUsageActivityDayRow _item_1;
          final String _tmpLocalDate;
          if (_stmt.isNull(_columnIndexOfLocalDate)) {
            _tmpLocalDate = null;
          } else {
            _tmpLocalDate = _stmt.getText(_columnIndexOfLocalDate);
          }
          final long _tmpTokens;
          _tmpTokens = _stmt.getLong(_columnIndexOfTokens);
          _item_1 = new TokenUsageActivityDayRow(_tmpLocalDate,_tmpTokens);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearPricing(final String configId, final String provider, final String model,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        UPDATE token_stats_models\n"
            + "        SET billingMode = NULL,\n"
            + "            currency = NULL,\n"
            + "            inputPricePerMillion = NULL,\n"
            + "            cachedInputPricePerMillion = NULL,\n"
            + "            cacheWritePricePerMillion = NULL,\n"
            + "            outputPricePerMillion = NULL,\n"
            + "            pricePerRequest = NULL\n"
            + "        WHERE configId = ? AND provider = ? AND model = ?\n"
            + "        ";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (configId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, configId);
        }
        _argIndex = 2;
        if (provider == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, provider);
        }
        _argIndex = 3;
        if (model == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, model);
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteEmptyStatsModels(final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        DELETE FROM token_stats_models\n"
            + "        WHERE billingMode IS NULL\n"
            + "            AND currency IS NULL\n"
            + "            AND inputPricePerMillion IS NULL\n"
            + "            AND cachedInputPricePerMillion IS NULL\n"
            + "            AND cacheWritePricePerMillion IS NULL\n"
            + "            AND outputPricePerMillion IS NULL\n"
            + "            AND pricePerRequest IS NULL\n"
            + "        ";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
