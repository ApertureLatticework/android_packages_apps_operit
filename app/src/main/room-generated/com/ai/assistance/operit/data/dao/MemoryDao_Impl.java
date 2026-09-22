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
import com.ai.assistance.operit.data.model.Embedding;
import com.ai.assistance.operit.data.model.Memory;
import com.ai.assistance.operit.data.model.MemoryDbConverters;
import com.ai.assistance.operit.data.model.MemoryTag;
import com.ai.assistance.operit.data.model.MemoryTagJunction;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class MemoryDao_Impl implements MemoryDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Memory> __insertAdapterOfMemory;

  private final MemoryDbConverters __memoryDbConverters = new MemoryDbConverters();

  private final EntityInsertAdapter<MemoryTag> __insertAdapterOfMemoryTag;

  private final EntityInsertAdapter<MemoryTagJunction> __insertAdapterOfMemoryTagJunction;

  private final EntityDeleteOrUpdateAdapter<Memory> __updateAdapterOfMemory;

  public MemoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfMemory = new EntityInsertAdapter<Memory>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `memory` (`id`,`uuid`,`title`,`content`,`contentType`,`source`,`credibility`,`importance`,`documentPath`,`isDocumentNode`,`chunkIndexFilePath`,`folderPath`,`embedding`,`createdAt`,`updatedAt`,`lastAccessedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Memory entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUuid() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getUuid());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getTitle());
        }
        if (entity.getContent() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getContent());
        }
        if (entity.getContentType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getContentType());
        }
        if (entity.getSource() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getSource());
        }
        statement.bindDouble(7, entity.getCredibility());
        statement.bindDouble(8, entity.getImportance());
        if (entity.getDocumentPath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getDocumentPath());
        }
        final int _tmp = entity.isDocumentNode() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getChunkIndexFilePath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getChunkIndexFilePath());
        }
        if (entity.getFolderPath() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getFolderPath());
        }
        final byte[] _tmp_1 = __memoryDbConverters.embeddingToDb(entity.getEmbedding());
        if (_tmp_1 == null) {
          statement.bindNull(13);
        } else {
          statement.bindBlob(13, _tmp_1);
        }
        final Long _tmp_2 = __memoryDbConverters.dateToDb(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_2);
        }
        final Long _tmp_3 = __memoryDbConverters.dateToDb(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_3);
        }
        final Long _tmp_4 = __memoryDbConverters.dateToDb(entity.getLastAccessedAt());
        if (_tmp_4 == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, _tmp_4);
        }
      }
    };
    this.__insertAdapterOfMemoryTag = new EntityInsertAdapter<MemoryTag>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `memory_tag` (`id`,`name`) VALUES (nullif(?, 0),?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MemoryTag entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
      }
    };
    this.__insertAdapterOfMemoryTagJunction = new EntityInsertAdapter<MemoryTagJunction>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `memory_tag_junction` (`memoryId`,`tagId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MemoryTagJunction entity) {
        statement.bindLong(1, entity.getMemoryId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__updateAdapterOfMemory = new EntityDeleteOrUpdateAdapter<Memory>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `memory` SET `id` = ?,`uuid` = ?,`title` = ?,`content` = ?,`contentType` = ?,`source` = ?,`credibility` = ?,`importance` = ?,`documentPath` = ?,`isDocumentNode` = ?,`chunkIndexFilePath` = ?,`folderPath` = ?,`embedding` = ?,`createdAt` = ?,`updatedAt` = ?,`lastAccessedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Memory entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getUuid() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getUuid());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getTitle());
        }
        if (entity.getContent() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getContent());
        }
        if (entity.getContentType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getContentType());
        }
        if (entity.getSource() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getSource());
        }
        statement.bindDouble(7, entity.getCredibility());
        statement.bindDouble(8, entity.getImportance());
        if (entity.getDocumentPath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getDocumentPath());
        }
        final int _tmp = entity.isDocumentNode() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getChunkIndexFilePath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getChunkIndexFilePath());
        }
        if (entity.getFolderPath() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getFolderPath());
        }
        final byte[] _tmp_1 = __memoryDbConverters.embeddingToDb(entity.getEmbedding());
        if (_tmp_1 == null) {
          statement.bindNull(13);
        } else {
          statement.bindBlob(13, _tmp_1);
        }
        final Long _tmp_2 = __memoryDbConverters.dateToDb(entity.getCreatedAt());
        if (_tmp_2 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_2);
        }
        final Long _tmp_3 = __memoryDbConverters.dateToDb(entity.getUpdatedAt());
        if (_tmp_3 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_3);
        }
        final Long _tmp_4 = __memoryDbConverters.dateToDb(entity.getLastAccessedAt());
        if (_tmp_4 == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, _tmp_4);
        }
        statement.bindLong(17, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Memory memory, final Continuation<? super Long> $completion) {
    if (memory == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMemory.insertAndReturnId(_connection, memory);
    }, $completion);
  }

  @Override
  public Object insertTag(final MemoryTag tag, final Continuation<? super Long> $completion) {
    if (tag == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMemoryTag.insertAndReturnId(_connection, tag);
    }, $completion);
  }

  @Override
  public Object linkTag(final MemoryTagJunction junction,
      final Continuation<? super Unit> $completion) {
    if (junction == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfMemoryTagJunction.insert(_connection, junction);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object update(final Memory memory, final Continuation<? super Unit> $completion) {
    if (memory == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfMemory.handle(_connection, memory);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object upsert(final Memory memory, final Continuation<? super Long> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return MemoryDao.super.upsert(memory, _cont);
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<Memory> memories,
      final Continuation<? super Unit> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return MemoryDao.super.upsertAll(memories, _cont);
    }, $completion);
  }

  @Override
  public Object getOrCreateTag(final String name,
      final Continuation<? super MemoryTag> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return MemoryDao.super.getOrCreateTag(name, _cont);
    }, $completion);
  }

  @Override
  public Object replaceTagsForMemory(final long memoryId, final List<Long> tagIds,
      final Continuation<? super Unit> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return MemoryDao.super.replaceTagsForMemory(memoryId, tagIds, _cont);
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<Memory>> $completion) {
    final String _sql = "SELECT * FROM memory";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final List<Memory> _result = new ArrayList<Memory>();
        while (_stmt.step()) {
          final Memory _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _item = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super Memory> $completion) {
    final String _sql = "SELECT * FROM memory WHERE id = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final Memory _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _result = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
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
      final Continuation<? super List<Memory>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM memory WHERE id IN (");
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
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final List<Memory> _result = new ArrayList<Memory>();
        while (_stmt.step()) {
          final Memory _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _item_1 = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getByUuid(final String uuid, final Continuation<? super Memory> $completion) {
    final String _sql = "SELECT * FROM memory WHERE uuid = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (uuid == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, uuid);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final Memory _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _result = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
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
  public Object getByUuids(final List<String> uuids,
      final Continuation<? super List<Memory>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM memory WHERE uuid IN (");
    final int _inputSize = uuids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        for (String _item : uuids) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final List<Memory> _result = new ArrayList<Memory>();
        while (_stmt.step()) {
          final Memory _item_1;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _item_1 = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getFirstByTitle(final String title,
      final Continuation<? super Memory> $completion) {
    final String _sql = "SELECT * FROM memory WHERE title = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (title == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, title);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final Memory _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _result = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
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
  public Object getByTitle(final String title,
      final Continuation<? super List<Memory>> $completion) {
    final String _sql = "SELECT * FROM memory WHERE title = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (title == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, title);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final List<Memory> _result = new ArrayList<Memory>();
        while (_stmt.step()) {
          final Memory _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _item = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getNonDocumentNodes(final Continuation<? super List<Memory>> $completion) {
    final String _sql = "SELECT * FROM memory WHERE isDocumentNode = 0";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final List<Memory> _result = new ArrayList<Memory>();
        while (_stmt.step()) {
          final Memory _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _item = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object titleContains(final String fragment,
      final Continuation<? super List<Memory>> $completion) {
    final String _sql = "SELECT * FROM memory WHERE title LIKE '%' || ? || '%'";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (fragment == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, fragment);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUuid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uuid");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfContent = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "content");
        final int _columnIndexOfContentType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "contentType");
        final int _columnIndexOfSource = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "source");
        final int _columnIndexOfCredibility = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "credibility");
        final int _columnIndexOfImportance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "importance");
        final int _columnIndexOfDocumentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "documentPath");
        final int _columnIndexOfIsDocumentNode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isDocumentNode");
        final int _columnIndexOfChunkIndexFilePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "chunkIndexFilePath");
        final int _columnIndexOfFolderPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "folderPath");
        final int _columnIndexOfEmbedding = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "embedding");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfLastAccessedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastAccessedAt");
        final List<Memory> _result = new ArrayList<Memory>();
        while (_stmt.step()) {
          final Memory _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpUuid;
          if (_stmt.isNull(_columnIndexOfUuid)) {
            _tmpUuid = null;
          } else {
            _tmpUuid = _stmt.getText(_columnIndexOfUuid);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpContentType;
          if (_stmt.isNull(_columnIndexOfContentType)) {
            _tmpContentType = null;
          } else {
            _tmpContentType = _stmt.getText(_columnIndexOfContentType);
          }
          final String _tmpSource;
          if (_stmt.isNull(_columnIndexOfSource)) {
            _tmpSource = null;
          } else {
            _tmpSource = _stmt.getText(_columnIndexOfSource);
          }
          final float _tmpCredibility;
          _tmpCredibility = (float) (_stmt.getDouble(_columnIndexOfCredibility));
          final float _tmpImportance;
          _tmpImportance = (float) (_stmt.getDouble(_columnIndexOfImportance));
          final String _tmpDocumentPath;
          if (_stmt.isNull(_columnIndexOfDocumentPath)) {
            _tmpDocumentPath = null;
          } else {
            _tmpDocumentPath = _stmt.getText(_columnIndexOfDocumentPath);
          }
          final boolean _tmpIsDocumentNode;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsDocumentNode));
          _tmpIsDocumentNode = _tmp != 0;
          final String _tmpChunkIndexFilePath;
          if (_stmt.isNull(_columnIndexOfChunkIndexFilePath)) {
            _tmpChunkIndexFilePath = null;
          } else {
            _tmpChunkIndexFilePath = _stmt.getText(_columnIndexOfChunkIndexFilePath);
          }
          final String _tmpFolderPath;
          if (_stmt.isNull(_columnIndexOfFolderPath)) {
            _tmpFolderPath = null;
          } else {
            _tmpFolderPath = _stmt.getText(_columnIndexOfFolderPath);
          }
          final Embedding _tmpEmbedding;
          final byte[] _tmp_1;
          if (_stmt.isNull(_columnIndexOfEmbedding)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getBlob(_columnIndexOfEmbedding);
          }
          _tmpEmbedding = __memoryDbConverters.embeddingFromDb(_tmp_1);
          final Date _tmpCreatedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = __memoryDbConverters.dateFromDb(_tmp_2);
          final Date _tmpUpdatedAt;
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfUpdatedAt);
          }
          _tmpUpdatedAt = __memoryDbConverters.dateFromDb(_tmp_3);
          final Date _tmpLastAccessedAt;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastAccessedAt);
          }
          _tmpLastAccessedAt = __memoryDbConverters.dateFromDb(_tmp_4);
          _item = new Memory(_tmpId,_tmpUuid,_tmpTitle,_tmpContent,_tmpContentType,_tmpSource,_tmpCredibility,_tmpImportance,_tmpDocumentPath,_tmpIsDocumentNode,_tmpChunkIndexFilePath,_tmpFolderPath,_tmpEmbedding,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastAccessedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getTagByName(final String name, final Continuation<? super MemoryTag> $completion) {
    final String _sql = "SELECT * FROM memory_tag WHERE name = ? LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (name == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, name);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final MemoryTag _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _result = new MemoryTag(_tmpId,_tmpName);
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
  public Object getTagPairs(final List<Long> memoryIds,
      final Continuation<? super List<MemoryTagPair>> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT j.memoryId AS memoryId, t.id AS id, t.name AS name");
    _stringBuilder.append("\n");
    _stringBuilder.append("        FROM memory_tag_junction j");
    _stringBuilder.append("\n");
    _stringBuilder.append("        INNER JOIN memory_tag t ON t.id = j.tagId");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE j.memoryId IN (");
    final int _inputSize = memoryIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ");
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
        final int _columnIndexOfMemoryId = 0;
        final int _columnIndexOfId = 1;
        final int _columnIndexOfName = 2;
        final List<MemoryTagPair> _result = new ArrayList<MemoryTagPair>();
        while (_stmt.step()) {
          final MemoryTagPair _item_1;
          final long _tmpMemoryId;
          _tmpMemoryId = _stmt.getLong(_columnIndexOfMemoryId);
          final MemoryTag _tmpTag;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _tmpTag = new MemoryTag(_tmpId,_tmpName);
          _item_1 = new MemoryTagPair(_tmpMemoryId,_tmpTag);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAllTagPairs(final Continuation<? super List<MemoryTagPair>> $completion) {
    final String _sql = "\n"
            + "        SELECT j.memoryId AS memoryId, t.id AS id, t.name AS name\n"
            + "        FROM memory_tag_junction j\n"
            + "        INNER JOIN memory_tag t ON t.id = j.tagId\n"
            + "        ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfMemoryId = 0;
        final int _columnIndexOfId = 1;
        final int _columnIndexOfName = 2;
        final List<MemoryTagPair> _result = new ArrayList<MemoryTagPair>();
        while (_stmt.step()) {
          final MemoryTagPair _item;
          final long _tmpMemoryId;
          _tmpMemoryId = _stmt.getLong(_columnIndexOfMemoryId);
          final MemoryTag _tmpTag;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _tmpTag = new MemoryTag(_tmpId,_tmpName);
          _item = new MemoryTagPair(_tmpMemoryId,_tmpTag);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteByIds(final List<Long> ids, final Continuation<? super Unit> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("DELETE FROM memory WHERE id IN (");
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
    final String _sql = "DELETE FROM memory WHERE id = ?";
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

  @Override
  public Object clearTagsForMemory(final long memoryId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM memory_tag_junction WHERE memoryId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, memoryId);
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
