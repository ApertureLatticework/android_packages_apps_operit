package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.MemoryAutoSaveCandidate;
import com.ai.assistance.operit.data.model.MemoryDbConverters;
import java.lang.Class;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class MemoryAutoSaveCandidateDao_Impl implements MemoryAutoSaveCandidateDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<MemoryAutoSaveCandidate> __insertAdapterOfMemoryAutoSaveCandidate;

  private final MemoryDbConverters __memoryDbConverters = new MemoryDbConverters();

  private final EntityDeleteOrUpdateAdapter<MemoryAutoSaveCandidate> __updateAdapterOfMemoryAutoSaveCandidate;

  public MemoryAutoSaveCandidateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfMemoryAutoSaveCandidate = new EntityInsertAdapter<MemoryAutoSaveCandidate>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `memory_auto_save_candidate` (`id`,`chatId`,`triggerMessageTimestamp`,`createdAt`,`updatedAt`,`status`,`attemptCount`,`lastError`,`sourceType`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MemoryAutoSaveCandidate entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getChatId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getChatId());
        }
        statement.bindLong(3, entity.getTriggerMessageTimestamp());
        final Long _tmp = __memoryDbConverters.dateToDb(entity.getCreatedAt());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp);
        }
        final Long _tmp_1 = __memoryDbConverters.dateToDb(entity.getUpdatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        if (entity.getStatus() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getStatus());
        }
        statement.bindLong(7, entity.getAttemptCount());
        if (entity.getLastError() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getLastError());
        }
        if (entity.getSourceType() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getSourceType());
        }
      }
    };
    this.__updateAdapterOfMemoryAutoSaveCandidate = new EntityDeleteOrUpdateAdapter<MemoryAutoSaveCandidate>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `memory_auto_save_candidate` SET `id` = ?,`chatId` = ?,`triggerMessageTimestamp` = ?,`createdAt` = ?,`updatedAt` = ?,`status` = ?,`attemptCount` = ?,`lastError` = ?,`sourceType` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MemoryAutoSaveCandidate entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getChatId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getChatId());
        }
        statement.bindLong(3, entity.getTriggerMessageTimestamp());
        final Long _tmp = __memoryDbConverters.dateToDb(entity.getCreatedAt());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp);
        }
        final Long _tmp_1 = __memoryDbConverters.dateToDb(entity.getUpdatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        if (entity.getStatus() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getStatus());
        }
        statement.bindLong(7, entity.getAttemptCount());
        if (entity.getLastError() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getLastError());
        }
        if (entity.getSourceType() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getSourceType());
        }
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final MemoryAutoSaveCandidate candidate,
      final Continuation<? super Long> $completion) {
    if (candidate == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMemoryAutoSaveCandidate.insertAndReturnId(_connection, candidate);
    }, $completion);
  }

  @Override
  public Object updateAll(final List<MemoryAutoSaveCandidate> candidates,
      final Continuation<? super Unit> $completion) {
    if (candidates == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfMemoryAutoSaveCandidate.handleMultiple(_connection, candidates);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getByStatuses(final List<String> statuses,
      final Continuation<? super List<MemoryAutoSaveCandidate>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM memory_auto_save_candidate WHERE status IN (");
    final int _inputSize = statuses.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ORDER BY createdAt ASC");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        for (String _item : statuses) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chatId");
        final int _columnIndexOfTriggerMessageTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "triggerMessageTimestamp");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfAttemptCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attemptCount");
        final int _columnIndexOfLastError = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastError");
        final int _columnIndexOfSourceType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceType");
        final List<MemoryAutoSaveCandidate> _result = new ArrayList<MemoryAutoSaveCandidate>();
        while (_stmt.step()) {
          final MemoryAutoSaveCandidate _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpTriggerMessageTimestamp;
          _tmpTriggerMessageTimestamp = _stmt.getLong(_columnIndexOfTriggerMessageTimestamp);
          final Date _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp);
          final Date _tmpUpdatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_1);
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final int _tmpAttemptCount;
          _tmpAttemptCount = (int) (_stmt.getLong(_columnIndexOfAttemptCount));
          final String _tmpLastError;
          if (_stmt.isNull(_columnIndexOfLastError)) {
            _tmpLastError = null;
          } else {
            _tmpLastError = _stmt.getText(_columnIndexOfLastError);
          }
          final String _tmpSourceType;
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null;
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType);
          }
          _item_1 = new MemoryAutoSaveCandidate(_tmpId,_tmpChatId,_tmpTriggerMessageTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpStatus,_tmpAttemptCount,_tmpLastError,_tmpSourceType);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id,
      final Continuation<? super MemoryAutoSaveCandidate> $completion) {
    final String _sql = "SELECT * FROM memory_auto_save_candidate WHERE id = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chatId");
        final int _columnIndexOfTriggerMessageTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "triggerMessageTimestamp");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfAttemptCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attemptCount");
        final int _columnIndexOfLastError = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastError");
        final int _columnIndexOfSourceType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceType");
        final MemoryAutoSaveCandidate _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpTriggerMessageTimestamp;
          _tmpTriggerMessageTimestamp = _stmt.getLong(_columnIndexOfTriggerMessageTimestamp);
          final Date _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp);
          final Date _tmpUpdatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_1);
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final int _tmpAttemptCount;
          _tmpAttemptCount = (int) (_stmt.getLong(_columnIndexOfAttemptCount));
          final String _tmpLastError;
          if (_stmt.isNull(_columnIndexOfLastError)) {
            _tmpLastError = null;
          } else {
            _tmpLastError = _stmt.getText(_columnIndexOfLastError);
          }
          final String _tmpSourceType;
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null;
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType);
          }
          _result = new MemoryAutoSaveCandidate(_tmpId,_tmpChatId,_tmpTriggerMessageTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpStatus,_tmpAttemptCount,_tmpLastError,_tmpSourceType);
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
  public Object getByIds(final List<Long> ids,
      final Continuation<? super List<MemoryAutoSaveCandidate>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM memory_auto_save_candidate WHERE id IN (");
    final int _inputSize = ids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        for (Long _item : ids) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindLong(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chatId");
        final int _columnIndexOfTriggerMessageTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "triggerMessageTimestamp");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfAttemptCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attemptCount");
        final int _columnIndexOfLastError = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastError");
        final int _columnIndexOfSourceType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceType");
        final List<MemoryAutoSaveCandidate> _result = new ArrayList<MemoryAutoSaveCandidate>();
        while (_stmt.step()) {
          final MemoryAutoSaveCandidate _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpTriggerMessageTimestamp;
          _tmpTriggerMessageTimestamp = _stmt.getLong(_columnIndexOfTriggerMessageTimestamp);
          final Date _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp);
          final Date _tmpUpdatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_1);
          final String _tmpStatus;
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus);
          }
          final int _tmpAttemptCount;
          _tmpAttemptCount = (int) (_stmt.getLong(_columnIndexOfAttemptCount));
          final String _tmpLastError;
          if (_stmt.isNull(_columnIndexOfLastError)) {
            _tmpLastError = null;
          } else {
            _tmpLastError = _stmt.getText(_columnIndexOfLastError);
          }
          final String _tmpSourceType;
          if (_stmt.isNull(_columnIndexOfSourceType)) {
            _tmpSourceType = null;
          } else {
            _tmpSourceType = _stmt.getText(_columnIndexOfSourceType);
          }
          _item_1 = new MemoryAutoSaveCandidate(_tmpId,_tmpChatId,_tmpTriggerMessageTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpStatus,_tmpAttemptCount,_tmpLastError,_tmpSourceType);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM memory_auto_save_candidate WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return Unit.INSTANCE;
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
