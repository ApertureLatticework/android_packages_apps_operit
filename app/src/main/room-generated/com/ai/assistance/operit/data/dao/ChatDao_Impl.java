package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteConnectionUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.CharacterCardChatStats;
import com.ai.assistance.operit.data.model.CharacterGroupChatStats;
import com.ai.assistance.operit.data.model.ChatEntity;
import java.lang.Class;
import java.lang.Integer;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ChatDao_Impl implements ChatDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<ChatEntity> __insertAdapterOfChatEntity;

  private final EntityDeleteOrUpdateAdapter<ChatEntity> __updateAdapterOfChatEntity;

  public ChatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfChatEntity = new EntityInsertAdapter<ChatEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chats` (`id`,`title`,`createdAt`,`updatedAt`,`inputTokens`,`outputTokens`,`currentWindowSize`,`group`,`displayOrder`,`workspace`,`workspaceEnv`,`parentChatId`,`characterCardName`,`characterGroupId`,`locked`,`pinned`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ChatEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindLong(4, entity.getUpdatedAt());
        statement.bindLong(5, entity.getInputTokens());
        statement.bindLong(6, entity.getOutputTokens());
        statement.bindLong(7, entity.getCurrentWindowSize());
        if (entity.getGroup() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getGroup());
        }
        statement.bindLong(9, entity.getDisplayOrder());
        if (entity.getWorkspace() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getWorkspace());
        }
        if (entity.getWorkspaceEnv() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getWorkspaceEnv());
        }
        if (entity.getParentChatId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getParentChatId());
        }
        if (entity.getCharacterCardName() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getCharacterCardName());
        }
        if (entity.getCharacterGroupId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindText(14, entity.getCharacterGroupId());
        }
        final int _tmp = entity.getLocked() ? 1 : 0;
        statement.bindLong(15, _tmp);
        final int _tmp_1 = entity.getPinned() ? 1 : 0;
        statement.bindLong(16, _tmp_1);
      }
    };
    this.__updateAdapterOfChatEntity = new EntityDeleteOrUpdateAdapter<ChatEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `chats` SET `id` = ?,`title` = ?,`createdAt` = ?,`updatedAt` = ?,`inputTokens` = ?,`outputTokens` = ?,`currentWindowSize` = ?,`group` = ?,`displayOrder` = ?,`workspace` = ?,`workspaceEnv` = ?,`parentChatId` = ?,`characterCardName` = ?,`characterGroupId` = ?,`locked` = ?,`pinned` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ChatEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindLong(4, entity.getUpdatedAt());
        statement.bindLong(5, entity.getInputTokens());
        statement.bindLong(6, entity.getOutputTokens());
        statement.bindLong(7, entity.getCurrentWindowSize());
        if (entity.getGroup() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getGroup());
        }
        statement.bindLong(9, entity.getDisplayOrder());
        if (entity.getWorkspace() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getWorkspace());
        }
        if (entity.getWorkspaceEnv() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getWorkspaceEnv());
        }
        if (entity.getParentChatId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getParentChatId());
        }
        if (entity.getCharacterCardName() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getCharacterCardName());
        }
        if (entity.getCharacterGroupId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindText(14, entity.getCharacterGroupId());
        }
        final int _tmp = entity.getLocked() ? 1 : 0;
        statement.bindLong(15, _tmp);
        final int _tmp_1 = entity.getPinned() ? 1 : 0;
        statement.bindLong(16, _tmp_1);
        if (entity.getId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindText(17, entity.getId());
        }
      }
    };
  }

  @Override
  public Object insertChat(final ChatEntity chat, final Continuation<? super Unit> $completion) {
    if (chat == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfChatEntity.insert(_connection, chat);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateChats(final List<ChatEntity> chats,
      final Continuation<? super Unit> $completion) {
    if (chats == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfChatEntity.handleMultiple(_connection, chats);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<ChatEntity>> getAllChats() {
    final String _sql = "SELECT * FROM chats ORDER BY pinned DESC, displayOrder ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"chats"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getTotalChatCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM chats";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
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
  public Object getAllChatsDirectly(final Continuation<? super List<ChatEntity>> $completion) {
    final String _sql = "SELECT * FROM chats ORDER BY pinned DESC, displayOrder ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getChatById(final String chatId,
      final Continuation<? super ChatEntity> $completion) {
    final String _sql = "SELECT * FROM chats WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final ChatEntity _result;
        if (_stmt.step()) {
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _result = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
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
  public Object getBranchesByParentId(final String parentChatId,
      final Continuation<? super List<ChatEntity>> $completion) {
    final String _sql = "SELECT * FROM chats WHERE parentChatId = ? ORDER BY pinned DESC, displayOrder ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (parentChatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, parentChatId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatEntity>> getBranchesByParentIdFlow(final String parentChatId) {
    final String _sql = "SELECT * FROM chats WHERE parentChatId = ? ORDER BY pinned DESC, displayOrder ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"chats"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (parentChatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, parentChatId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getMainChats(final Continuation<? super List<ChatEntity>> $completion) {
    final String _sql = "SELECT * FROM chats WHERE parentChatId IS NULL ORDER BY pinned DESC, displayOrder ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatEntity>> getMainChatsFlow() {
    final String _sql = "SELECT * FROM chats WHERE parentChatId IS NULL ORDER BY pinned DESC, displayOrder ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"chats"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<ChatEntity>> getChatsByCharacterCard(final String characterCardName) {
    final String _sql = "SELECT * FROM chats WHERE characterCardName = ? AND characterGroupId IS NULL ORDER BY pinned DESC, displayOrder ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"chats"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<ChatEntity>> getChatsByCharacterGroupId(final String characterGroupId) {
    final String _sql = "SELECT * FROM chats WHERE characterGroupId = ? ORDER BY pinned DESC, displayOrder ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"chats"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterGroupId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<ChatEntity>> getChatsByCharacterCardOrNull(final String characterCardName) {
    final String _sql = "SELECT * FROM chats WHERE characterCardName = ? OR (characterCardName IS NULL AND characterGroupId IS NULL) ORDER BY pinned DESC, displayOrder ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"chats"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfUpdatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "updatedAt");
        final int _columnIndexOfInputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "inputTokens");
        final int _columnIndexOfOutputTokens = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "outputTokens");
        final int _columnIndexOfCurrentWindowSize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentWindowSize");
        final int _columnIndexOfGroup = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "group");
        final int _columnIndexOfDisplayOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayOrder");
        final int _columnIndexOfWorkspace = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspace");
        final int _columnIndexOfWorkspaceEnv = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "workspaceEnv");
        final int _columnIndexOfParentChatId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentChatId");
        final int _columnIndexOfCharacterCardName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterCardName");
        final int _columnIndexOfCharacterGroupId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "characterGroupId");
        final int _columnIndexOfLocked = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locked");
        final int _columnIndexOfPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "pinned");
        final List<ChatEntity> _result = new ArrayList<ChatEntity>();
        while (_stmt.step()) {
          final ChatEntity _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt);
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCurrentWindowSize;
          _tmpCurrentWindowSize = _stmt.getLong(_columnIndexOfCurrentWindowSize);
          final String _tmpGroup;
          if (_stmt.isNull(_columnIndexOfGroup)) {
            _tmpGroup = null;
          } else {
            _tmpGroup = _stmt.getText(_columnIndexOfGroup);
          }
          final long _tmpDisplayOrder;
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder);
          final String _tmpWorkspace;
          if (_stmt.isNull(_columnIndexOfWorkspace)) {
            _tmpWorkspace = null;
          } else {
            _tmpWorkspace = _stmt.getText(_columnIndexOfWorkspace);
          }
          final String _tmpWorkspaceEnv;
          if (_stmt.isNull(_columnIndexOfWorkspaceEnv)) {
            _tmpWorkspaceEnv = null;
          } else {
            _tmpWorkspaceEnv = _stmt.getText(_columnIndexOfWorkspaceEnv);
          }
          final String _tmpParentChatId;
          if (_stmt.isNull(_columnIndexOfParentChatId)) {
            _tmpParentChatId = null;
          } else {
            _tmpParentChatId = _stmt.getText(_columnIndexOfParentChatId);
          }
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final boolean _tmpLocked;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfLocked));
          _tmpLocked = _tmp != 0;
          final boolean _tmpPinned;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPinned));
          _tmpPinned = _tmp_1 != 0;
          _item = new ChatEntity(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpUpdatedAt,_tmpInputTokens,_tmpOutputTokens,_tmpCurrentWindowSize,_tmpGroup,_tmpDisplayOrder,_tmpWorkspace,_tmpWorkspaceEnv,_tmpParentChatId,_tmpCharacterCardName,_tmpCharacterGroupId,_tmpLocked,_tmpPinned);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<CharacterCardChatStats>> getCharacterCardChatStats() {
    final String _sql = "\n"
            + "        SELECT \n"
            + "            c.characterCardName AS characterCardName,\n"
            + "            COUNT(c.id) AS chatCount,\n"
            + "            IFNULL(SUM(mc.messageCount), 0) AS messageCount\n"
            + "        FROM chats c\n"
            + "        LEFT JOIN (\n"
            + "            SELECT chatId, COUNT(*) AS messageCount\n"
            + "            FROM messages\n"
            + "            GROUP BY chatId\n"
            + "        ) mc ON c.id = mc.chatId\n"
            + "        WHERE c.characterGroupId IS NULL\n"
            + "        GROUP BY c.characterCardName\n"
            + "        ";
    return FlowUtil.createFlow(__db, false, new String[] {"chats", "messages"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfCharacterCardName = 0;
        final int _columnIndexOfChatCount = 1;
        final int _columnIndexOfMessageCount = 2;
        final List<CharacterCardChatStats> _result = new ArrayList<CharacterCardChatStats>();
        while (_stmt.step()) {
          final CharacterCardChatStats _item;
          final String _tmpCharacterCardName;
          if (_stmt.isNull(_columnIndexOfCharacterCardName)) {
            _tmpCharacterCardName = null;
          } else {
            _tmpCharacterCardName = _stmt.getText(_columnIndexOfCharacterCardName);
          }
          final int _tmpChatCount;
          _tmpChatCount = (int) (_stmt.getLong(_columnIndexOfChatCount));
          final int _tmpMessageCount;
          _tmpMessageCount = (int) (_stmt.getLong(_columnIndexOfMessageCount));
          _item = new CharacterCardChatStats(_tmpCharacterCardName,_tmpChatCount,_tmpMessageCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<CharacterGroupChatStats>> getCharacterGroupChatStats() {
    final String _sql = "\n"
            + "        SELECT \n"
            + "            c.characterGroupId AS characterGroupId,\n"
            + "            COUNT(c.id) AS chatCount,\n"
            + "            IFNULL(SUM(mc.messageCount), 0) AS messageCount\n"
            + "        FROM chats c\n"
            + "        LEFT JOIN (\n"
            + "            SELECT chatId, COUNT(*) AS messageCount\n"
            + "            FROM messages\n"
            + "            GROUP BY chatId\n"
            + "        ) mc ON c.id = mc.chatId\n"
            + "        WHERE c.characterCardName IS NULL\n"
            + "        GROUP BY c.characterGroupId\n"
            + "        ";
    return FlowUtil.createFlow(__db, false, new String[] {"chats", "messages"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfCharacterGroupId = 0;
        final int _columnIndexOfChatCount = 1;
        final int _columnIndexOfMessageCount = 2;
        final List<CharacterGroupChatStats> _result = new ArrayList<CharacterGroupChatStats>();
        while (_stmt.step()) {
          final CharacterGroupChatStats _item;
          final String _tmpCharacterGroupId;
          if (_stmt.isNull(_columnIndexOfCharacterGroupId)) {
            _tmpCharacterGroupId = null;
          } else {
            _tmpCharacterGroupId = _stmt.getText(_columnIndexOfCharacterGroupId);
          }
          final int _tmpChatCount;
          _tmpChatCount = (int) (_stmt.getLong(_columnIndexOfChatCount));
          final int _tmpMessageCount;
          _tmpMessageCount = (int) (_stmt.getLong(_columnIndexOfMessageCount));
          _item = new CharacterGroupChatStats(_tmpCharacterGroupId,_tmpChatCount,_tmpMessageCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object deleteChat(final String chatId, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM chats WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatMetadata(final String chatId, final String title, final long timestamp,
      final long inputTokens, final long outputTokens, final long currentWindowSize,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET updatedAt = ?, title = ?, inputTokens = ?, outputTokens = ?, currentWindowSize = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (title == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, title);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, inputTokens);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, outputTokens);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, currentWindowSize);
        _argIndex = 6;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatTitle(final String chatId, final String title, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET title = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (title == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, title);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatWorkspace(final String chatId, final String workspace,
      final String workspaceEnv, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `workspace` = ?, `workspaceEnv` = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (workspace == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, workspace);
        }
        _argIndex = 2;
        if (workspaceEnv == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, workspaceEnv);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatTitleAndWorkspace(final String chatId, final String title,
      final String workspace, final String workspaceEnv, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET title = ?, `workspace` = ?, `workspaceEnv` = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (title == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, title);
        }
        _argIndex = 2;
        if (workspace == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, workspace);
        }
        _argIndex = 3;
        if (workspaceEnv == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, workspaceEnv);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 5;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatGroup(final String chatId, final String group, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `group` = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (group == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, group);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatCharacterCardName(final String chatId, final String characterCardName,
      final long timestamp, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = ?, characterGroupId = NULL, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatCharacterGroupId(final String chatId, final String characterGroupId,
      final long timestamp, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = NULL, characterGroupId = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterGroupId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatCharacterBinding(final String chatId, final String characterCardName,
      final String characterGroupId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = ?, characterGroupId = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _argIndex = 2;
        if (characterGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterGroupId);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatLocked(final String chatId, final boolean locked, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET locked = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = locked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatPinned(final String chatId, final boolean pinned, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET pinned = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = pinned ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateChatOrderAndGroup(final String chatId, final long displayOrder,
      final String group, final long timestamp, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET displayOrder = ?, `group` = ?, updatedAt = ? WHERE id = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, displayOrder);
        _argIndex = 2;
        if (group == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, group);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateGroupName(final String oldName, final String newName,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `group` = ? WHERE `group` = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (newName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, newName);
        }
        _argIndex = 2;
        if (oldName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, oldName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateGroupNameForCharacter(final String oldName, final String newName,
      final String characterCardName, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `group` = ? WHERE `group` = ? AND characterCardName = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (newName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, newName);
        }
        _argIndex = 2;
        if (oldName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, oldName);
        }
        _argIndex = 3;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteChatsInGroup(final String groupName,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM chats WHERE `group` = ? AND locked = 0";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, groupName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteChatsInGroupForCharacter(final String groupName,
      final String characterCardName, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM chats WHERE `group` = ? AND characterCardName = ? AND locked = 0";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, groupName);
        }
        _argIndex = 2;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object removeGroupFromChats(final String groupName, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `group` = NULL, updatedAt = ? WHERE `group` = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, groupName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object removeGroupFromLockedChats(final String groupName, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `group` = NULL, updatedAt = ? WHERE `group` = ? AND locked = 1";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, groupName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object removeGroupFromChatsForCharacter(final String groupName,
      final String characterCardName, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `group` = NULL, updatedAt = ? WHERE `group` = ? AND characterCardName = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, groupName);
        }
        _argIndex = 3;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object removeGroupFromLockedChatsForCharacter(final String groupName,
      final String characterCardName, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET `group` = NULL, updatedAt = ? WHERE `group` = ? AND characterCardName = ? AND locked = 1";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, groupName);
        }
        _argIndex = 3;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearCharacterCardBinding(final String characterCardName, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = NULL, updatedAt = ? WHERE characterCardName = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteUnlockedChatsByCharacterCardName(final String characterCardName,
      final Continuation<? super Integer> $completion) {
    final String _sql = "DELETE FROM chats WHERE characterCardName = ? AND locked = 0";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterCardName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterCardName);
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteUnlockedUnboundChats(final Continuation<? super Integer> $completion) {
    final String _sql = "DELETE FROM chats WHERE characterCardName IS NULL AND characterGroupId IS NULL AND locked = 0";
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

  @Override
  public Object renameCharacterCardBinding(final String oldName, final String newName,
      final long timestamp, final Continuation<? super Integer> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = ?, updatedAt = ? WHERE characterCardName = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (newName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, newName);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (oldName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, oldName);
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object renameCharacterGroupBinding(final String sourceGroupId, final String targetGroupId,
      final long timestamp, final Continuation<? super Integer> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = NULL, characterGroupId = ?, updatedAt = ? WHERE characterGroupId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (targetGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, targetGroupId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        if (sourceGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, sourceGroupId);
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object assignCharacterCardToUnbound(final String newName, final long timestamp,
      final Continuation<? super Integer> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = ?, updatedAt = ? WHERE characterCardName IS NULL AND characterGroupId IS NULL";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (newName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, newName);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object assignCharacterGroupToUnbound(final String targetGroupId, final long timestamp,
      final Continuation<? super Integer> $completion) {
    final String _sql = "UPDATE chats SET characterCardName = NULL, characterGroupId = ?, updatedAt = ? WHERE characterGroupId IS NULL AND characterCardName IS NULL";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (targetGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, targetGroupId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateCharacterCardForChats(final List<String> chatIds, final String newName,
      final long timestamp, final Continuation<? super Integer> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("UPDATE chats SET characterCardName = ");
    _stringBuilder.append("?");
    _stringBuilder.append(", characterGroupId = NULL, updatedAt = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" WHERE id IN (");
    final int _inputSize = chatIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (newName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, newName);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        for (String _item : chatIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateCharacterGroupForChats(final List<String> chatIds,
      final String characterGroupId, final long timestamp,
      final Continuation<? super Integer> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("UPDATE chats SET characterCardName = NULL, characterGroupId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(", updatedAt = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" WHERE id IN (");
    final int _inputSize = chatIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (characterGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, characterGroupId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        for (String _item : chatIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearCharacterGroupForChats(final List<String> chatIds, final long timestamp,
      final Continuation<? super Integer> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("UPDATE chats SET characterGroupId = NULL, updatedAt = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" WHERE id IN (");
    final int _inputSize = chatIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        for (String _item : chatIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearCharacterGroupBinding(final String sourceGroupId, final long timestamp,
      final Continuation<? super Integer> $completion) {
    final String _sql = "UPDATE chats SET characterGroupId = NULL, updatedAt = ? WHERE characterGroupId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        if (sourceGroupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, sourceGroupId);
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateGroupForChats(final List<String> chatIds, final String groupName,
      final long timestamp, final Continuation<? super Integer> $completion) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("UPDATE chats SET `group` = ");
    _stringBuilder.append("?");
    _stringBuilder.append(", updatedAt = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" WHERE id IN (");
    final int _inputSize = chatIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (groupName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, groupName);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        for (String _item : chatIds) {
          if (_item == null) {
            _stmt.bindNull(_argIndex);
          } else {
            _stmt.bindText(_argIndex, _item);
          }
          _argIndex++;
        }
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
