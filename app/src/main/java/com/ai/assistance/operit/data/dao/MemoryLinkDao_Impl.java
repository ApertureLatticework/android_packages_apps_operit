package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteConnectionUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.MemoryLink;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class MemoryLinkDao_Impl implements MemoryLinkDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<MemoryLink> __insertAdapterOfMemoryLink;

  private final EntityDeleteOrUpdateAdapter<MemoryLink> __updateAdapterOfMemoryLink;

  public MemoryLinkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfMemoryLink = new EntityInsertAdapter<MemoryLink>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `memory_link` (`id`,`type`,`weight`,`description`,`sourceId`,`targetId`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MemoryLink entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getType());
        }
        statement.bindDouble(3, entity.getWeight());
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getDescription());
        }
        statement.bindLong(5, entity.getSourceId());
        statement.bindLong(6, entity.getTargetId());
      }
    };
    this.__updateAdapterOfMemoryLink = new EntityDeleteOrUpdateAdapter<MemoryLink>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `memory_link` SET `id` = ?,`type` = ?,`weight` = ?,`description` = ?,`sourceId` = ?,`targetId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MemoryLink entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getType());
        }
        statement.bindDouble(3, entity.getWeight());
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getDescription());
        }
        statement.bindLong(5, entity.getSourceId());
        statement.bindLong(6, entity.getTargetId());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final MemoryLink link, final Continuation<? super Long> $completion) {
    if (link == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMemoryLink.insertAndReturnId(_connection, link);
    }, $completion);
  }

  @Override
  public Object insertAll(final List<MemoryLink> links,
      final Continuation<? super List<Long>> $completion) {
    if (links == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMemoryLink.insertAndReturnIdsList(_connection, links);
    }, $completion);
  }

  @Override
  public Object update(final MemoryLink link, final Continuation<? super Unit> $completion) {
    if (link == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfMemoryLink.handle(_connection, link);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateAll(final List<MemoryLink> links,
      final Continuation<? super Unit> $completion) {
    if (links == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfMemoryLink.handleMultiple(_connection, links);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<MemoryLink>> $completion) {
    final String _sql = "SELECT * FROM memory_link";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfWeight = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weight");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfSourceId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceId");
        final int _columnIndexOfTargetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetId");
        final List<MemoryLink> _result = new ArrayList<MemoryLink>();
        while (_stmt.step()) {
          final MemoryLink _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final float _tmpWeight;
          _tmpWeight = (float) (_stmt.getDouble(_columnIndexOfWeight));
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpSourceId;
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _stmt.getLong(_columnIndexOfTargetId);
          _item = new MemoryLink(_tmpId,_tmpType,_tmpWeight,_tmpDescription,_tmpSourceId,_tmpTargetId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super MemoryLink> $completion) {
    final String _sql = "SELECT * FROM memory_link WHERE id = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfWeight = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weight");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfSourceId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceId");
        final int _columnIndexOfTargetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetId");
        final MemoryLink _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final float _tmpWeight;
          _tmpWeight = (float) (_stmt.getDouble(_columnIndexOfWeight));
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpSourceId;
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _stmt.getLong(_columnIndexOfTargetId);
          _result = new MemoryLink(_tmpId,_tmpType,_tmpWeight,_tmpDescription,_tmpSourceId,_tmpTargetId);
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
      final Continuation<? super List<MemoryLink>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM memory_link WHERE id IN (");
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
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfWeight = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weight");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfSourceId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceId");
        final int _columnIndexOfTargetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetId");
        final List<MemoryLink> _result = new ArrayList<MemoryLink>();
        while (_stmt.step()) {
          final MemoryLink _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final float _tmpWeight;
          _tmpWeight = (float) (_stmt.getDouble(_columnIndexOfWeight));
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpSourceId;
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _stmt.getLong(_columnIndexOfTargetId);
          _item_1 = new MemoryLink(_tmpId,_tmpType,_tmpWeight,_tmpDescription,_tmpSourceId,_tmpTargetId);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getOutgoing(final long memoryId,
      final Continuation<? super List<MemoryLink>> $completion) {
    final String _sql = "SELECT * FROM memory_link WHERE sourceId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, memoryId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfWeight = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weight");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfSourceId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceId");
        final int _columnIndexOfTargetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetId");
        final List<MemoryLink> _result = new ArrayList<MemoryLink>();
        while (_stmt.step()) {
          final MemoryLink _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final float _tmpWeight;
          _tmpWeight = (float) (_stmt.getDouble(_columnIndexOfWeight));
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpSourceId;
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _stmt.getLong(_columnIndexOfTargetId);
          _item = new MemoryLink(_tmpId,_tmpType,_tmpWeight,_tmpDescription,_tmpSourceId,_tmpTargetId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getIncoming(final long memoryId,
      final Continuation<? super List<MemoryLink>> $completion) {
    final String _sql = "SELECT * FROM memory_link WHERE targetId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, memoryId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfWeight = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weight");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfSourceId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceId");
        final int _columnIndexOfTargetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetId");
        final List<MemoryLink> _result = new ArrayList<MemoryLink>();
        while (_stmt.step()) {
          final MemoryLink _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final float _tmpWeight;
          _tmpWeight = (float) (_stmt.getDouble(_columnIndexOfWeight));
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpSourceId;
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _stmt.getLong(_columnIndexOfTargetId);
          _item = new MemoryLink(_tmpId,_tmpType,_tmpWeight,_tmpDescription,_tmpSourceId,_tmpTargetId);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getOutgoingFor(final List<Long> memoryIds,
      final Continuation<? super List<MemoryLink>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM memory_link WHERE sourceId IN (");
    final int _inputSize = memoryIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        for (Long _item : memoryIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindLong(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfWeight = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weight");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfSourceId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceId");
        final int _columnIndexOfTargetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetId");
        final List<MemoryLink> _result = new ArrayList<MemoryLink>();
        while (_stmt.step()) {
          final MemoryLink _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final float _tmpWeight;
          _tmpWeight = (float) (_stmt.getDouble(_columnIndexOfWeight));
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpSourceId;
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _stmt.getLong(_columnIndexOfTargetId);
          _item_1 = new MemoryLink(_tmpId,_tmpType,_tmpWeight,_tmpDescription,_tmpSourceId,_tmpTargetId);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getIncomingFor(final List<Long> memoryIds,
      final Continuation<? super List<MemoryLink>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM memory_link WHERE targetId IN (");
    final int _inputSize = memoryIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        for (Long _item : memoryIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindLong(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfWeight = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "weight");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfSourceId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sourceId");
        final int _columnIndexOfTargetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetId");
        final List<MemoryLink> _result = new ArrayList<MemoryLink>();
        while (_stmt.step()) {
          final MemoryLink _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          final float _tmpWeight;
          _tmpWeight = (float) (_stmt.getDouble(_columnIndexOfWeight));
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final long _tmpSourceId;
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId);
          final long _tmpTargetId;
          _tmpTargetId = _stmt.getLong(_columnIndexOfTargetId);
          _item_1 = new MemoryLink(_tmpId,_tmpType,_tmpWeight,_tmpDescription,_tmpSourceId,_tmpTargetId);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteByIds(final Collection<Long> ids,
      final Continuation<? super Unit> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("DELETE FROM memory_link WHERE id IN (");
    final int _inputSize = ids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
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
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Integer> $completion) {
    final String _sql = "DELETE FROM memory_link WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
