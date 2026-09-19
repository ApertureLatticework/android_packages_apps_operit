package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteConnectionUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.ChatMessageLocatorPreview;
import com.ai.assistance.operit.data.model.MessageEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<MessageEntity> __insertAdapterOfMessageEntity;

  private final EntityDeleteOrUpdateAdapter<MessageEntity> __updateAdapterOfMessageEntity;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfMessageEntity = new EntityInsertAdapter<MessageEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `messages` (`messageId`,`chatId`,`sender`,`content`,`timestamp`,`orderIndex`,`roleName`,`selectedVariantIndex`,`provider`,`modelName`,`inputTokens`,`outputTokens`,`cachedInputTokens`,`sentAt`,`outputDurationMs`,`waitDurationMs`,`completedAt`,`displayMode`,`isFavorite`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindLong(1, entity.getMessageId());
        if (entity.getChatId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getChatId());
        }
        if (entity.getSender() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getSender());
        }
        if (entity.getContent() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getContent());
        }
        statement.bindLong(5, entity.getTimestamp());
        statement.bindLong(6, entity.getOrderIndex());
        if (entity.getRoleName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getRoleName());
        }
        statement.bindLong(8, entity.getSelectedVariantIndex());
        if (entity.getProvider() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getProvider());
        }
        if (entity.getModelName() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getModelName());
        }
        statement.bindLong(11, entity.getInputTokens());
        statement.bindLong(12, entity.getOutputTokens());
        statement.bindLong(13, entity.getCachedInputTokens());
        statement.bindLong(14, entity.getSentAt());
        statement.bindLong(15, entity.getOutputDurationMs());
        statement.bindLong(16, entity.getWaitDurationMs());
        statement.bindLong(17, entity.getCompletedAt());
        if (entity.getDisplayMode() == null) {
          statement.bindNull(18);
        } else {
          statement.bindText(18, entity.getDisplayMode());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(19, _tmp);
      }
    };
    this.__updateAdapterOfMessageEntity = new EntityDeleteOrUpdateAdapter<MessageEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `messages` SET `messageId` = ?,`chatId` = ?,`sender` = ?,`content` = ?,`timestamp` = ?,`orderIndex` = ?,`roleName` = ?,`selectedVariantIndex` = ?,`provider` = ?,`modelName` = ?,`inputTokens` = ?,`outputTokens` = ?,`cachedInputTokens` = ?,`sentAt` = ?,`outputDurationMs` = ?,`waitDurationMs` = ?,`completedAt` = ?,`displayMode` = ?,`isFavorite` = ? WHERE `messageId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindLong(1, entity.getMessageId());
        if (entity.getChatId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getChatId());
        }
        if (entity.getSender() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getSender());
        }
        if (entity.getContent() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getContent());
        }
        statement.bindLong(5, entity.getTimestamp());
        statement.bindLong(6, entity.getOrderIndex());
        if (entity.getRoleName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getRoleName());
        }
        statement.bindLong(8, entity.getSelectedVariantIndex());
        if (entity.getProvider() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getProvider());
        }
        if (entity.getModelName() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getModelName());
        }
        statement.bindLong(11, entity.getInputTokens());
        statement.bindLong(12, entity.getOutputTokens());
        statement.bindLong(13, entity.getCachedInputTokens());
        statement.bindLong(14, entity.getSentAt());
        statement.bindLong(15, entity.getOutputDurationMs());
        statement.bindLong(16, entity.getWaitDurationMs());
        statement.bindLong(17, entity.getCompletedAt());
        if (entity.getDisplayMode() == null) {
          statement.bindNull(18);
        } else {
          statement.bindText(18, entity.getDisplayMode());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(19, _tmp);
        statement.bindLong(20, entity.getMessageId());
      }
    };
  }

  @Override
  public Object insertMessage(final MessageEntity message,
      final Continuation<? super Long> $completion) {
    if (message == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMessageEntity.insertAndReturnId(_connection, message);
    }, $completion);
  }

  @Override
  public Object insertMessages(final List<MessageEntity> messages,
      final Continuation<? super Unit> $completion) {
    if (messages == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfMessageEntity.insert(_connection, messages);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateMessage(final MessageEntity message,
      final Continuation<? super Unit> $completion) {
    if (message == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfMessageEntity.handle(_connection, message);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getTotalMessageCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM messages";
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
  public Object countMessagesForChatUpToTimestamp(final String chatId,
      final Long upToTimestampInclusive, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM messages WHERE chatId = ? AND (? IS NULL OR timestamp <= ?)";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 2;
        if (upToTimestampInclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, upToTimestampInclusive);
        }
        _argIndex = 3;
        if (upToTimestampInclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, upToTimestampInclusive);
        }
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
  public Object getLocatorPreviewsForChat(final String chatId, final int previewCharCount,
      final Continuation<? super List<ChatMessageLocatorPreview>> $completion) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            (\n"
            + "                SELECT COUNT(*)\n"
            + "                FROM messages AS earlier\n"
            + "                WHERE earlier.chatId = messages.chatId\n"
            + "                    AND earlier.timestamp < messages.timestamp\n"
            + "            ) AS messageIndex,\n"
            + "            timestamp AS timestamp,\n"
            + "            sender AS sender,\n"
            + "            CASE\n"
            + "                WHEN sender = 'user' AND displayMode = 'HIDDEN_PLACEHOLDER' THEN ''\n"
            + "                ELSE SUBSTR(content, 1, ?)\n"
            + "            END AS previewContent,\n"
            + "            CASE\n"
            + "                WHEN sender = 'user' AND displayMode = 'HIDDEN_PLACEHOLDER' THEN 0\n"
            + "                ELSE LENGTH(content)\n"
            + "            END AS contentLength,\n"
            + "            displayMode AS displayMode,\n"
            + "            isFavorite AS isFavorite\n"
            + "        FROM messages\n"
            + "        WHERE chatId = ?\n"
            + "        ORDER BY timestamp ASC\n"
            + "        ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, previewCharCount);
        _argIndex = 2;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        final int _columnIndexOfMessageIndex = 0;
        final int _columnIndexOfTimestamp = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfPreviewContent = 3;
        final int _columnIndexOfContentLength = 4;
        final int _columnIndexOfDisplayMode = 5;
        final int _columnIndexOfIsFavorite = 6;
        final List<ChatMessageLocatorPreview> _result = new ArrayList<ChatMessageLocatorPreview>();
        while (_stmt.step()) {
          final ChatMessageLocatorPreview _item;
          final Integer _tmpMessageIndex;
          if (_stmt.isNull(_columnIndexOfMessageIndex)) {
            _tmpMessageIndex = null;
          } else {
            _tmpMessageIndex = (int) (_stmt.getLong(_columnIndexOfMessageIndex));
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpPreviewContent;
          if (_stmt.isNull(_columnIndexOfPreviewContent)) {
            _tmpPreviewContent = null;
          } else {
            _tmpPreviewContent = _stmt.getText(_columnIndexOfPreviewContent);
          }
          final int _tmpContentLength;
          _tmpContentLength = (int) (_stmt.getLong(_columnIndexOfContentLength));
          final String _tmpDisplayMode;
          if (_stmt.isNull(_columnIndexOfDisplayMode)) {
            _tmpDisplayMode = null;
          } else {
            _tmpDisplayMode = _stmt.getText(_columnIndexOfDisplayMode);
          }
          final boolean _tmpIsFavorite;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsFavorite));
          _tmpIsFavorite = _tmp != 0;
          _item = new ChatMessageLocatorPreview(_tmpMessageIndex,_tmpTimestamp,_tmpSender,_tmpPreviewContent,_tmpContentLength,_tmpDisplayMode,_tmpIsFavorite);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object searchLocatorPreviewsForChat(final String chatId, final String query,
      final int previewCharCount,
      final Continuation<? super List<ChatMessageLocatorPreview>> $completion) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            (\n"
            + "                SELECT COUNT(*)\n"
            + "                FROM messages AS earlier\n"
            + "                WHERE earlier.chatId = messages.chatId\n"
            + "                    AND earlier.timestamp < messages.timestamp\n"
            + "            ) AS messageIndex,\n"
            + "            timestamp AS timestamp,\n"
            + "            sender AS sender,\n"
            + "            SUBSTR(\n"
            + "                content,\n"
            + "                MAX(1, INSTR(LOWER(content), LOWER(?)) - (? / 2)),\n"
            + "                ?\n"
            + "            ) AS previewContent,\n"
            + "            LENGTH(content) AS contentLength,\n"
            + "            displayMode AS displayMode,\n"
            + "            isFavorite AS isFavorite\n"
            + "        FROM messages\n"
            + "        WHERE chatId = ?\n"
            + "            AND NOT (sender = 'user' AND displayMode = 'HIDDEN_PLACEHOLDER')\n"
            + "            AND INSTR(LOWER(content), LOWER(?)) > 0\n"
            + "        ORDER BY timestamp ASC\n"
            + "        ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, previewCharCount);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, previewCharCount);
        _argIndex = 4;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 5;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        final int _columnIndexOfMessageIndex = 0;
        final int _columnIndexOfTimestamp = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfPreviewContent = 3;
        final int _columnIndexOfContentLength = 4;
        final int _columnIndexOfDisplayMode = 5;
        final int _columnIndexOfIsFavorite = 6;
        final List<ChatMessageLocatorPreview> _result = new ArrayList<ChatMessageLocatorPreview>();
        while (_stmt.step()) {
          final ChatMessageLocatorPreview _item;
          final Integer _tmpMessageIndex;
          if (_stmt.isNull(_columnIndexOfMessageIndex)) {
            _tmpMessageIndex = null;
          } else {
            _tmpMessageIndex = (int) (_stmt.getLong(_columnIndexOfMessageIndex));
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpPreviewContent;
          if (_stmt.isNull(_columnIndexOfPreviewContent)) {
            _tmpPreviewContent = null;
          } else {
            _tmpPreviewContent = _stmt.getText(_columnIndexOfPreviewContent);
          }
          final int _tmpContentLength;
          _tmpContentLength = (int) (_stmt.getLong(_columnIndexOfContentLength));
          final String _tmpDisplayMode;
          if (_stmt.isNull(_columnIndexOfDisplayMode)) {
            _tmpDisplayMode = null;
          } else {
            _tmpDisplayMode = _stmt.getText(_columnIndexOfDisplayMode);
          }
          final boolean _tmpIsFavorite;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsFavorite));
          _tmpIsFavorite = _tmp != 0;
          _item = new ChatMessageLocatorPreview(_tmpMessageIndex,_tmpTimestamp,_tmpSender,_tmpPreviewContent,_tmpContentLength,_tmpDisplayMode,_tmpIsFavorite);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object existsMessagesBeforeTimestamp(final String chatId,
      final long beforeTimestampExclusive, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM messages WHERE chatId = ? AND timestamp < ? LIMIT 1)";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, beforeTimestampExclusive);
        final Boolean _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp == null ? null : _tmp != 0;
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
  public Object existsMessagesAfterTimestamp(final String chatId,
      final long afterTimestampExclusive, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM messages WHERE chatId = ? AND timestamp > ? LIMIT 1)";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, afterTimestampExclusive);
        final Boolean _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp == null ? null : _tmp != 0;
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
  public Object getLatestSummaryTimestamp(final String chatId,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT timestamp FROM messages WHERE chatId = ? AND sender = 'summary' ORDER BY timestamp DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        final Long _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getLong(0);
          }
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
  public Object getLatestSummaryTimestampBefore(final String chatId,
      final long beforeTimestampExclusive, final Continuation<? super Long> $completion) {
    final String _sql = "SELECT timestamp FROM messages WHERE chatId = ? AND sender = 'summary' AND timestamp < ? ORDER BY timestamp DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, beforeTimestampExclusive);
        final Long _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getLong(0);
          }
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
  public Object getLatestSummaryTimestampUpTo(final String chatId,
      final long upToTimestampInclusive, final Continuation<? super Long> $completion) {
    final String _sql = "SELECT timestamp FROM messages WHERE chatId = ? AND sender = 'summary' AND timestamp <= ? ORDER BY timestamp DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, upToTimestampInclusive);
        final Long _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getLong(0);
          }
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
  public Object existsUserMessage(final String chatId,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM messages WHERE chatId = ? AND sender = 'user' LIMIT 1)";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        final Boolean _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp == null ? null : _tmp != 0;
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
  public Object existsAnyMessage(final String chatId,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM messages WHERE chatId = ? LIMIT 1)";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        final Boolean _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp == null ? null : _tmp != 0;
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
  public Object getMessageCountsByChatId(
      final Continuation<? super List<ChatMessageCount>> $completion) {
    final String _sql = "SELECT chatId AS chatId, COUNT(*) AS count FROM messages GROUP BY chatId";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfChatId = 0;
        final int _columnIndexOfCount = 1;
        final List<ChatMessageCount> _result = new ArrayList<ChatMessageCount>();
        while (_stmt.step()) {
          final ChatMessageCount _item;
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final int _tmpCount;
          _tmpCount = (int) (_stmt.getLong(_columnIndexOfCount));
          _item = new ChatMessageCount(_tmpChatId,_tmpCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getMaxOrderIndex(final String chatId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MAX(orderIndex) FROM messages WHERE chatId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
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
  public Object searchChatIdsByContent(final String query,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT chatId FROM messages WHERE content LIKE '%' || ? || '%' ESCAPE '\\' COLLATE NOCASE";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        final List<String> _result = new ArrayList<String>();
        while (_stmt.step()) {
          final String _item;
          if (_stmt.isNull(0)) {
            _item = null;
          } else {
            _item = _stmt.getText(0);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object copyMessagesToChat(final String sourceChatId, final String targetChatId,
      final Long upToTimestampInclusive, final Continuation<? super Unit> $completion) {
    final String _sql = "\n"
            + "        INSERT INTO messages (\n"
            + "            chatId,\n"
            + "            sender,\n"
            + "            content,\n"
            + "            timestamp,\n"
            + "            orderIndex,\n"
            + "            roleName,\n"
            + "            selectedVariantIndex,\n"
            + "            provider,\n"
            + "            modelName,\n"
            + "            inputTokens,\n"
            + "            outputTokens,\n"
            + "            cachedInputTokens,\n"
            + "            sentAt,\n"
            + "            outputDurationMs,\n"
            + "            waitDurationMs,\n"
            + "            completedAt,\n"
            + "            displayMode,\n"
            + "            isFavorite\n"
            + "        )\n"
            + "        SELECT\n"
            + "            ?,\n"
            + "            sender,\n"
            + "            content,\n"
            + "            timestamp,\n"
            + "            orderIndex,\n"
            + "            roleName,\n"
            + "            selectedVariantIndex,\n"
            + "            provider,\n"
            + "            modelName,\n"
            + "            inputTokens,\n"
            + "            outputTokens,\n"
            + "            cachedInputTokens,\n"
            + "            sentAt,\n"
            + "            outputDurationMs,\n"
            + "            waitDurationMs,\n"
            + "            completedAt,\n"
            + "            displayMode,\n"
            + "            isFavorite\n"
            + "        FROM messages\n"
            + "        WHERE chatId = ?\n"
            + "            AND (? IS NULL OR timestamp <= ?)\n"
            + "        ";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (targetChatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, targetChatId);
        }
        _argIndex = 2;
        if (sourceChatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, sourceChatId);
        }
        _argIndex = 3;
        if (upToTimestampInclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, upToTimestampInclusive);
        }
        _argIndex = 4;
        if (upToTimestampInclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, upToTimestampInclusive);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateMessageContent(final long messageId, final String content,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE messages SET content = ? WHERE messageId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (content == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, content);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, messageId);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllMessagesForChat(final String chatId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM messages WHERE chatId = ?";
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
  public Object deleteMessagesFrom(final String chatId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM messages WHERE chatId = ? AND timestamp >= ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteMessageByTimestamp(final String chatId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM messages WHERE chatId = ? AND timestamp = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateSelectedVariantIndex(final String chatId, final long timestamp,
      final int selectedVariantIndex, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE messages SET selectedVariantIndex = ? WHERE chatId = ? AND timestamp = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, selectedVariantIndex);
        _argIndex = 2;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateMessageFavorite(final String chatId, final long timestamp,
      final boolean isFavorite, final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE messages SET isFavorite = ? WHERE chatId = ? AND timestamp = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object renameRoleName(final String oldName, final String newName,
      final Continuation<? super Integer> $completion) {
    final String _sql = "UPDATE messages SET roleName = ? WHERE roleName = ?";
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
