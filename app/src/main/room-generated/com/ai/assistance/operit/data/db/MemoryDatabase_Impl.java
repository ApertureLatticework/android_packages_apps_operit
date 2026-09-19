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
import com.ai.assistance.operit.data.dao.DocumentChunkDao;
import com.ai.assistance.operit.data.dao.DocumentChunkDao_Impl;
import com.ai.assistance.operit.data.dao.MemoryAutoSaveCandidateDao;
import com.ai.assistance.operit.data.dao.MemoryAutoSaveCandidateDao_Impl;
import com.ai.assistance.operit.data.dao.MemoryDao;
import com.ai.assistance.operit.data.dao.MemoryDao_Impl;
import com.ai.assistance.operit.data.dao.MemoryLinkDao;
import com.ai.assistance.operit.data.dao.MemoryLinkDao_Impl;
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
public final class MemoryDatabase_Impl extends MemoryDatabase {
  private volatile MemoryDao _memoryDao;

  private volatile MemoryLinkDao _memoryLinkDao;

  private volatile DocumentChunkDao _documentChunkDao;

  private volatile MemoryAutoSaveCandidateDao _memoryAutoSaveCandidateDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(1, "c4f05cbdb1099c5e8c4975e952609763", "05ab24ee3cc1e4c5b1d035a4b7f86314") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `memory` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `uuid` TEXT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `contentType` TEXT NOT NULL, `source` TEXT NOT NULL, `credibility` REAL NOT NULL, `importance` REAL NOT NULL, `documentPath` TEXT, `isDocumentNode` INTEGER NOT NULL, `chunkIndexFilePath` TEXT, `folderPath` TEXT, `embedding` BLOB, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `lastAccessedAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_memory_uuid` ON `memory` (`uuid`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_memory_folderPath` ON `memory` (`folderPath`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `memory_tag` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_memory_tag_name` ON `memory_tag` (`name`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `memory_tag_junction` (`memoryId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`memoryId`, `tagId`), FOREIGN KEY(`memoryId`) REFERENCES `memory`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tagId`) REFERENCES `memory_tag`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_memory_tag_junction_tagId` ON `memory_tag_junction` (`tagId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `memory_link` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `weight` REAL NOT NULL, `description` TEXT NOT NULL, `sourceId` INTEGER NOT NULL, `targetId` INTEGER NOT NULL, FOREIGN KEY(`sourceId`) REFERENCES `memory`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`targetId`) REFERENCES `memory`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_memory_link_sourceId` ON `memory_link` (`sourceId`)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_memory_link_targetId` ON `memory_link` (`targetId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `document_chunk` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `memoryId` INTEGER NOT NULL, `content` TEXT NOT NULL, `chunkIndex` INTEGER NOT NULL, `embedding` BLOB, FOREIGN KEY(`memoryId`) REFERENCES `memory`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_document_chunk_memoryId` ON `document_chunk` (`memoryId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `memory_auto_save_candidate` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `chatId` TEXT NOT NULL, `triggerMessageTimestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `status` TEXT NOT NULL, `attemptCount` INTEGER NOT NULL, `lastError` TEXT NOT NULL, `sourceType` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE INDEX IF NOT EXISTS `index_memory_auto_save_candidate_chatId` ON `memory_auto_save_candidate` (`chatId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c4f05cbdb1099c5e8c4975e952609763')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `memory`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `memory_tag`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `memory_tag_junction`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `memory_link`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `document_chunk`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `memory_auto_save_candidate`");
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
        final Map<String, TableInfo.Column> _columnsMemory = new HashMap<String, TableInfo.Column>(16);
        _columnsMemory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("uuid", new TableInfo.Column("uuid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("contentType", new TableInfo.Column("contentType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("credibility", new TableInfo.Column("credibility", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("importance", new TableInfo.Column("importance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("documentPath", new TableInfo.Column("documentPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("isDocumentNode", new TableInfo.Column("isDocumentNode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("chunkIndexFilePath", new TableInfo.Column("chunkIndexFilePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("folderPath", new TableInfo.Column("folderPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("embedding", new TableInfo.Column("embedding", "BLOB", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemory.put("lastAccessedAt", new TableInfo.Column("lastAccessedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMemory = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesMemory = new HashSet<TableInfo.Index>(2);
        _indicesMemory.add(new TableInfo.Index("index_memory_uuid", false, Arrays.asList("uuid"), Arrays.asList("ASC")));
        _indicesMemory.add(new TableInfo.Index("index_memory_folderPath", false, Arrays.asList("folderPath"), Arrays.asList("ASC")));
        final TableInfo _infoMemory = new TableInfo("memory", _columnsMemory, _foreignKeysMemory, _indicesMemory);
        final TableInfo _existingMemory = TableInfo.read(connection, "memory");
        if (!_infoMemory.equals(_existingMemory)) {
          return new RoomOpenDelegate.ValidationResult(false, "memory(com.ai.assistance.operit.data.model.Memory).\n"
                  + " Expected:\n" + _infoMemory + "\n"
                  + " Found:\n" + _existingMemory);
        }
        final Map<String, TableInfo.Column> _columnsMemoryTag = new HashMap<String, TableInfo.Column>(2);
        _columnsMemoryTag.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryTag.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMemoryTag = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesMemoryTag = new HashSet<TableInfo.Index>(1);
        _indicesMemoryTag.add(new TableInfo.Index("index_memory_tag_name", false, Arrays.asList("name"), Arrays.asList("ASC")));
        final TableInfo _infoMemoryTag = new TableInfo("memory_tag", _columnsMemoryTag, _foreignKeysMemoryTag, _indicesMemoryTag);
        final TableInfo _existingMemoryTag = TableInfo.read(connection, "memory_tag");
        if (!_infoMemoryTag.equals(_existingMemoryTag)) {
          return new RoomOpenDelegate.ValidationResult(false, "memory_tag(com.ai.assistance.operit.data.model.MemoryTag).\n"
                  + " Expected:\n" + _infoMemoryTag + "\n"
                  + " Found:\n" + _existingMemoryTag);
        }
        final Map<String, TableInfo.Column> _columnsMemoryTagJunction = new HashMap<String, TableInfo.Column>(2);
        _columnsMemoryTagJunction.put("memoryId", new TableInfo.Column("memoryId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryTagJunction.put("tagId", new TableInfo.Column("tagId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMemoryTagJunction = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMemoryTagJunction.add(new TableInfo.ForeignKey("memory", "CASCADE", "NO ACTION", Arrays.asList("memoryId"), Arrays.asList("id")));
        _foreignKeysMemoryTagJunction.add(new TableInfo.ForeignKey("memory_tag", "CASCADE", "NO ACTION", Arrays.asList("tagId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesMemoryTagJunction = new HashSet<TableInfo.Index>(1);
        _indicesMemoryTagJunction.add(new TableInfo.Index("index_memory_tag_junction_tagId", false, Arrays.asList("tagId"), Arrays.asList("ASC")));
        final TableInfo _infoMemoryTagJunction = new TableInfo("memory_tag_junction", _columnsMemoryTagJunction, _foreignKeysMemoryTagJunction, _indicesMemoryTagJunction);
        final TableInfo _existingMemoryTagJunction = TableInfo.read(connection, "memory_tag_junction");
        if (!_infoMemoryTagJunction.equals(_existingMemoryTagJunction)) {
          return new RoomOpenDelegate.ValidationResult(false, "memory_tag_junction(com.ai.assistance.operit.data.model.MemoryTagJunction).\n"
                  + " Expected:\n" + _infoMemoryTagJunction + "\n"
                  + " Found:\n" + _existingMemoryTagJunction);
        }
        final Map<String, TableInfo.Column> _columnsMemoryLink = new HashMap<String, TableInfo.Column>(6);
        _columnsMemoryLink.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryLink.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryLink.put("weight", new TableInfo.Column("weight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryLink.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryLink.put("sourceId", new TableInfo.Column("sourceId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryLink.put("targetId", new TableInfo.Column("targetId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMemoryLink = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMemoryLink.add(new TableInfo.ForeignKey("memory", "CASCADE", "NO ACTION", Arrays.asList("sourceId"), Arrays.asList("id")));
        _foreignKeysMemoryLink.add(new TableInfo.ForeignKey("memory", "CASCADE", "NO ACTION", Arrays.asList("targetId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesMemoryLink = new HashSet<TableInfo.Index>(2);
        _indicesMemoryLink.add(new TableInfo.Index("index_memory_link_sourceId", false, Arrays.asList("sourceId"), Arrays.asList("ASC")));
        _indicesMemoryLink.add(new TableInfo.Index("index_memory_link_targetId", false, Arrays.asList("targetId"), Arrays.asList("ASC")));
        final TableInfo _infoMemoryLink = new TableInfo("memory_link", _columnsMemoryLink, _foreignKeysMemoryLink, _indicesMemoryLink);
        final TableInfo _existingMemoryLink = TableInfo.read(connection, "memory_link");
        if (!_infoMemoryLink.equals(_existingMemoryLink)) {
          return new RoomOpenDelegate.ValidationResult(false, "memory_link(com.ai.assistance.operit.data.model.MemoryLink).\n"
                  + " Expected:\n" + _infoMemoryLink + "\n"
                  + " Found:\n" + _existingMemoryLink);
        }
        final Map<String, TableInfo.Column> _columnsDocumentChunk = new HashMap<String, TableInfo.Column>(5);
        _columnsDocumentChunk.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocumentChunk.put("memoryId", new TableInfo.Column("memoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocumentChunk.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocumentChunk.put("chunkIndex", new TableInfo.Column("chunkIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocumentChunk.put("embedding", new TableInfo.Column("embedding", "BLOB", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDocumentChunk = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDocumentChunk.add(new TableInfo.ForeignKey("memory", "CASCADE", "NO ACTION", Arrays.asList("memoryId"), Arrays.asList("id")));
        final Set<TableInfo.Index> _indicesDocumentChunk = new HashSet<TableInfo.Index>(1);
        _indicesDocumentChunk.add(new TableInfo.Index("index_document_chunk_memoryId", false, Arrays.asList("memoryId"), Arrays.asList("ASC")));
        final TableInfo _infoDocumentChunk = new TableInfo("document_chunk", _columnsDocumentChunk, _foreignKeysDocumentChunk, _indicesDocumentChunk);
        final TableInfo _existingDocumentChunk = TableInfo.read(connection, "document_chunk");
        if (!_infoDocumentChunk.equals(_existingDocumentChunk)) {
          return new RoomOpenDelegate.ValidationResult(false, "document_chunk(com.ai.assistance.operit.data.model.DocumentChunk).\n"
                  + " Expected:\n" + _infoDocumentChunk + "\n"
                  + " Found:\n" + _existingDocumentChunk);
        }
        final Map<String, TableInfo.Column> _columnsMemoryAutoSaveCandidate = new HashMap<String, TableInfo.Column>(9);
        _columnsMemoryAutoSaveCandidate.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("chatId", new TableInfo.Column("chatId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("triggerMessageTimestamp", new TableInfo.Column("triggerMessageTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("attemptCount", new TableInfo.Column("attemptCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("lastError", new TableInfo.Column("lastError", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryAutoSaveCandidate.put("sourceType", new TableInfo.Column("sourceType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysMemoryAutoSaveCandidate = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesMemoryAutoSaveCandidate = new HashSet<TableInfo.Index>(1);
        _indicesMemoryAutoSaveCandidate.add(new TableInfo.Index("index_memory_auto_save_candidate_chatId", false, Arrays.asList("chatId"), Arrays.asList("ASC")));
        final TableInfo _infoMemoryAutoSaveCandidate = new TableInfo("memory_auto_save_candidate", _columnsMemoryAutoSaveCandidate, _foreignKeysMemoryAutoSaveCandidate, _indicesMemoryAutoSaveCandidate);
        final TableInfo _existingMemoryAutoSaveCandidate = TableInfo.read(connection, "memory_auto_save_candidate");
        if (!_infoMemoryAutoSaveCandidate.equals(_existingMemoryAutoSaveCandidate)) {
          return new RoomOpenDelegate.ValidationResult(false, "memory_auto_save_candidate(com.ai.assistance.operit.data.model.MemoryAutoSaveCandidate).\n"
                  + " Expected:\n" + _infoMemoryAutoSaveCandidate + "\n"
                  + " Found:\n" + _existingMemoryAutoSaveCandidate);
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
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "memory", "memory_tag", "memory_tag_junction", "memory_link", "document_chunk", "memory_auto_save_candidate");
  }

  @Override
  public void clearAllTables() {
    super.performClear(true, "memory", "memory_tag", "memory_tag_junction", "memory_link", "document_chunk", "memory_auto_save_candidate");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MemoryDao.class, MemoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MemoryLinkDao.class, MemoryLinkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DocumentChunkDao.class, DocumentChunkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MemoryAutoSaveCandidateDao.class, MemoryAutoSaveCandidateDao_Impl.getRequiredConverters());
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
  public MemoryDao memoryDao() {
    if (_memoryDao != null) {
      return _memoryDao;
    } else {
      synchronized(this) {
        if(_memoryDao == null) {
          _memoryDao = new MemoryDao_Impl(this);
        }
        return _memoryDao;
      }
    }
  }

  @Override
  public MemoryLinkDao memoryLinkDao() {
    if (_memoryLinkDao != null) {
      return _memoryLinkDao;
    } else {
      synchronized(this) {
        if(_memoryLinkDao == null) {
          _memoryLinkDao = new MemoryLinkDao_Impl(this);
        }
        return _memoryLinkDao;
      }
    }
  }

  @Override
  public DocumentChunkDao documentChunkDao() {
    if (_documentChunkDao != null) {
      return _documentChunkDao;
    } else {
      synchronized(this) {
        if(_documentChunkDao == null) {
          _documentChunkDao = new DocumentChunkDao_Impl(this);
        }
        return _documentChunkDao;
      }
    }
  }

  @Override
  public MemoryAutoSaveCandidateDao memoryAutoSaveCandidateDao() {
    if (_memoryAutoSaveCandidateDao != null) {
      return _memoryAutoSaveCandidateDao;
    } else {
      synchronized(this) {
        if(_memoryAutoSaveCandidateDao == null) {
          _memoryAutoSaveCandidateDao = new MemoryAutoSaveCandidateDao_Impl(this);
        }
        return _memoryAutoSaveCandidateDao;
      }
    }
  }
}
