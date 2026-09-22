package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.DocumentChunk;
import com.ai.assistance.operit.data.model.Embedding;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class DocumentChunkDao_Impl implements DocumentChunkDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<DocumentChunk> __insertAdapterOfDocumentChunk;

  private final MemoryDbConverters __memoryDbConverters = new MemoryDbConverters();

  private final EntityDeleteOrUpdateAdapter<DocumentChunk> __updateAdapterOfDocumentChunk;

  public DocumentChunkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfDocumentChunk = new EntityInsertAdapter<DocumentChunk>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `document_chunk` (`id`,`memoryId`,`content`,`chunkIndex`,`embedding`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final DocumentChunk entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMemoryId());
        if (entity.getContent() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getContent());
        }
        statement.bindLong(4, entity.getChunkIndex());
        final byte[] _tmp = __memoryDbConverters.embeddingToDb(entity.getEmbedding());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindBlob(5, _tmp);
        }
      }
    };
    this.__updateAdapterOfDocumentChunk = new EntityDeleteOrUpdateAdapter<DocumentChunk>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `document_chunk` SET `id` = ?,`memoryId` = ?,`content` = ?,`chunkIndex` = ?,`embedding` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final DocumentChunk entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMemoryId());
        if (entity.getContent() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getContent());
        }
        statement.bindLong(4, entity.getChunkIndex());
        final byte[] _tmp = __memoryDbConverters.embeddingToDb(entity.getEmbedding());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindBlob(5, _tmp);
        }
        statement.bindLong(6, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final DocumentChunk chunk, final Continuation<? super Long> $completion) {
    if (chunk == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfDocumentChunk.insertAndReturnId(_connection, chunk);
    }, $completion);
  }

  @Override
  public Object insertAll(final List<DocumentChunk> chunks,
      final Continuation<? super List<Long>> $completion) {
    if (chunks == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfDocumentChunk.insertAndReturnIdsList(_connection, chunks);
    }, $completion);
  }

  @Override
  public Object update(final DocumentChunk chunk, final Continuation<? super Unit> $completion) {
    if (chunk == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfDocumentChunk.handle(_connection, chunk);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateAll(final List<DocumentChunk> chunks,
      final Continuation<? super Unit> $completion) {
    if (chunks == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfDocumentChunk.handleMultiple(_connection, chunks);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<DocumentChunk>> $completion) {
    final String _sql = "SELECT * FROM document_chunk";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfMemoryId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "memoryId");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfChunkIndex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndex");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final List<DocumentChunk> _result = new ArrayList<DocumentChunk>();
        while (_stmt.step()) {
          final DocumentChunk _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpMemoryId;
          _tmpMemoryId = _stmt.getLong(_columnIndexOfMemoryId);
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final int _tmpChunkIndex;
          _tmpChunkIndex = (int) (_stmt.getLong(_columnIndexOfChunkIndex));
          final Embedding _tmpEmbedding;
          final byte[] _tmp;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp);
          _item = new DocumentChunk(_tmpId,_tmpMemoryId,_tmpContent,_tmpChunkIndex,_tmpEmbedding);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super DocumentChunk> $completion) {
    final String _sql = "SELECT * FROM document_chunk WHERE id = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfMemoryId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "memoryId");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfChunkIndex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndex");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final DocumentChunk _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpMemoryId;
          _tmpMemoryId = _stmt.getLong(_columnIndexOfMemoryId);
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final int _tmpChunkIndex;
          _tmpChunkIndex = (int) (_stmt.getLong(_columnIndexOfChunkIndex));
          final Embedding _tmpEmbedding;
          final byte[] _tmp;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp);
          _result = new DocumentChunk(_tmpId,_tmpMemoryId,_tmpContent,_tmpChunkIndex,_tmpEmbedding);
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
      final Continuation<? super List<DocumentChunk>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM document_chunk WHERE id IN (");
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
        final int _columnIndexOfMemoryId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "memoryId");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfChunkIndex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndex");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final List<DocumentChunk> _result = new ArrayList<DocumentChunk>();
        while (_stmt.step()) {
          final DocumentChunk _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpMemoryId;
          _tmpMemoryId = _stmt.getLong(_columnIndexOfMemoryId);
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final int _tmpChunkIndex;
          _tmpChunkIndex = (int) (_stmt.getLong(_columnIndexOfChunkIndex));
          final Embedding _tmpEmbedding;
          final byte[] _tmp;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp);
          _item_1 = new DocumentChunk(_tmpId,_tmpMemoryId,_tmpContent,_tmpChunkIndex,_tmpEmbedding);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getByMemoryId(final long memoryId,
      final Continuation<? super List<DocumentChunk>> $completion) {
    final String _sql = "SELECT * FROM document_chunk WHERE memoryId = ? ORDER BY chunkIndex ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, memoryId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfMemoryId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "memoryId");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfChunkIndex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndex");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final List<DocumentChunk> _result = new ArrayList<DocumentChunk>();
        while (_stmt.step()) {
          final DocumentChunk _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpMemoryId;
          _tmpMemoryId = _stmt.getLong(_columnIndexOfMemoryId);
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final int _tmpChunkIndex;
          _tmpChunkIndex = (int) (_stmt.getLong(_columnIndexOfChunkIndex));
          final Embedding _tmpEmbedding;
          final byte[] _tmp;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp);
          _item = new DocumentChunk(_tmpId,_tmpMemoryId,_tmpContent,_tmpChunkIndex,_tmpEmbedding);
          _result.add(_item);
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
    _stringBuilder.append("DELETE FROM document_chunk WHERE id IN (");
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
