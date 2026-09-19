package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.MessageEntity;
import com.ai.assistance.operit.data.model.MessageVariantEntity;
import java.lang.Class;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ChatContentDao_Impl extends ChatContentDao {
  private final RoomDatabase __db;

  public ChatContentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public Object getMessagesForChat(final String chatId,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChat(chatId, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatFromTimestampAsc(final String chatId,
      final long startTimestampInclusive,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatFromTimestampAsc(chatId, startTimestampInclusive, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatWindowAsc(final String chatId, final long startTimestampInclusive,
      final long endTimestampInclusive,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatWindowAsc(chatId, startTimestampInclusive, endTimestampInclusive, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatAsc(final String chatId, final int limit,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatAsc(chatId, limit, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatDesc(final String chatId, final int limit,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatDesc(chatId, limit, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatAscRange(final String chatId, final int offset, final int limit,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatAscRange(chatId, offset, limit, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatDescRange(final String chatId, final int offset, final int limit,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatDescRange(chatId, offset, limit, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatAfterTimestampExclusiveAsc(final String chatId,
      final long afterTimestampExclusive, final int limit,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatAfterTimestampExclusiveAsc(chatId, afterTimestampExclusive, limit, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatInRangeAsc(final String chatId,
      final Long afterTimestampExclusive, final Long beforeTimestampExclusive,
      final Long upToTimestampInclusive,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatInRangeAsc(chatId, afterTimestampExclusive, beforeTimestampExclusive, upToTimestampInclusive, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatBeforeTimestampDesc(final String chatId, final long maxTimestamp,
      final int limit, final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatBeforeTimestampDesc(chatId, maxTimestamp, limit, _cont);
    }, $completion);
  }

  @Override
  public Object getMessagesForChatBeforeTimestampExclusiveDesc(final String chatId,
      final long beforeTimestampExclusive, final int limit,
      final Continuation<? super List<MessageEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessagesForChatBeforeTimestampExclusiveDesc(chatId, beforeTimestampExclusive, limit, _cont);
    }, $completion);
  }

  @Override
  public Object getMessageByTimestamp(final String chatId, final long timestamp,
      final Continuation<? super MessageEntity> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getMessageByTimestamp(chatId, timestamp, _cont);
    }, $completion);
  }

  @Override
  public Object getVariantsForChat(final String chatId,
      final Continuation<? super List<MessageVariantEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getVariantsForChat(chatId, _cont);
    }, $completion);
  }

  @Override
  public Object getVariantsForMessages(final String chatId, final List<Long> messageTimestamps,
      final Continuation<? super List<MessageVariantEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getVariantsForMessages(chatId, messageTimestamps, _cont);
    }, $completion);
  }

  @Override
  public Object getVariantsForMessage(final String chatId, final long messageTimestamp,
      final Continuation<? super List<MessageVariantEntity>> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getVariantsForMessage(chatId, messageTimestamp, _cont);
    }, $completion);
  }

  @Override
  public Object getVariantForMessage(final String chatId, final long messageTimestamp,
      final int variantIndex, final Continuation<? super MessageVariantEntity> $completion) {
    return DBUtil.performInTransactionSuspending(__db, (_cont) -> {
      return ChatContentDao_Impl.super.getVariantForMessage(chatId, messageTimestamp, variantIndex, _cont);
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChat(final String chatId,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? ORDER BY timestamp ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatFromTimestampAsc(final String chatId,
      final long startTimestampInclusive,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? AND timestamp >= ? ORDER BY timestamp ASC";
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
        _stmt.bindLong(_argIndex, startTimestampInclusive);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatWindowAsc(final String chatId,
      final long startTimestampInclusive, final long endTimestampInclusive,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? AND timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC";
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
        _stmt.bindLong(_argIndex, startTimestampInclusive);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, endTimestampInclusive);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatAsc(final String chatId, final int limit,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? ORDER BY timestamp ASC LIMIT ?";
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
        _stmt.bindLong(_argIndex, limit);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatDesc(final String chatId, final int limit,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? ORDER BY timestamp DESC LIMIT ?";
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
        _stmt.bindLong(_argIndex, limit);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatAscRange(final String chatId, final int offset,
      final int limit, final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? ORDER BY timestamp ASC LIMIT ? OFFSET ?";
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
        _stmt.bindLong(_argIndex, limit);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, offset);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatDescRange(final String chatId, final int offset,
      final int limit, final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? ORDER BY timestamp DESC LIMIT ? OFFSET ?";
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
        _stmt.bindLong(_argIndex, limit);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, offset);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatAfterTimestampExclusiveAsc(final String chatId,
      final long afterTimestampExclusive, final int limit,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? AND timestamp > ? ORDER BY timestamp ASC LIMIT ?";
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
        _argIndex = 3;
        _stmt.bindLong(_argIndex, limit);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatInRangeAsc(final String chatId,
      final Long afterTimestampExclusive, final Long beforeTimestampExclusive,
      final Long upToTimestampInclusive,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? AND (? IS NULL OR timestamp > ?) AND (? IS NULL OR timestamp < ?) AND (? IS NULL OR timestamp <= ?) ORDER BY timestamp ASC";
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
        if (afterTimestampExclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, afterTimestampExclusive);
        }
        _argIndex = 3;
        if (afterTimestampExclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, afterTimestampExclusive);
        }
        _argIndex = 4;
        if (beforeTimestampExclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, beforeTimestampExclusive);
        }
        _argIndex = 5;
        if (beforeTimestampExclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, beforeTimestampExclusive);
        }
        _argIndex = 6;
        if (upToTimestampInclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, upToTimestampInclusive);
        }
        _argIndex = 7;
        if (upToTimestampInclusive == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, upToTimestampInclusive);
        }
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatBeforeTimestampDesc(final String chatId,
      final long maxTimestamp, final int limit,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? AND timestamp <= ? ORDER BY timestamp DESC LIMIT ?";
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
        _stmt.bindLong(_argIndex, maxTimestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, limit);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessagesForChatBeforeTimestampExclusiveDesc(final String chatId,
      final long beforeTimestampExclusive, final int limit,
      final Continuation<? super List<MessageContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? AND timestamp < ? ORDER BY timestamp DESC LIMIT ?";
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
        _argIndex = 3;
        _stmt.bindLong(_argIndex, limit);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final List<MessageContentRow> _result = new ArrayList<MessageContentRow>();
        while (_stmt.step()) {
          final MessageContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _item = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryMessageByTimestamp(final String chatId, final long timestamp,
      final Continuation<? super MessageContentRow> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        messageId,\n"
            + "        chatId,\n"
            + "        sender,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        timestamp,\n"
            + "        orderIndex,\n"
            + "        roleName,\n"
            + "        selectedVariantIndex,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        displayMode,\n"
            + "        isFavorite,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM messages\n"
            + "     WHERE chatId = ? AND timestamp = ? LIMIT 1";
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
        _stmt.bindLong(_argIndex, timestamp);
        final int _columnIndexOfMessageId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfSender = 2;
        final int _columnIndexOfContent = 3;
        final int _columnIndexOfTimestamp = 4;
        final int _columnIndexOfOrderIndex = 5;
        final int _columnIndexOfRoleName = 6;
        final int _columnIndexOfSelectedVariantIndex = 7;
        final int _columnIndexOfProvider = 8;
        final int _columnIndexOfModelName = 9;
        final int _columnIndexOfInputTokens = 10;
        final int _columnIndexOfOutputTokens = 11;
        final int _columnIndexOfCachedInputTokens = 12;
        final int _columnIndexOfSentAt = 13;
        final int _columnIndexOfOutputDurationMs = 14;
        final int _columnIndexOfWaitDurationMs = 15;
        final int _columnIndexOfCompletedAt = 16;
        final int _columnIndexOfDisplayMode = 17;
        final int _columnIndexOfIsFavorite = 18;
        final int _columnIndexOfContentCharacterCount = 19;
        final MessageContentRow _result;
        if (_stmt.step()) {
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageEntity _tmpMessage;
          final long _tmpMessageId;
          _tmpMessageId = _stmt.getLong(_columnIndexOfMessageId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final String _tmpSender;
          if (_stmt.isNull(_columnIndexOfSender)) {
            _tmpSender = null;
          } else {
            _tmpSender = _stmt.getText(_columnIndexOfSender);
          }
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmpOrderIndex;
          _tmpOrderIndex = (int) (_stmt.getLong(_columnIndexOfOrderIndex));
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final int _tmpSelectedVariantIndex;
          _tmpSelectedVariantIndex = (int) (_stmt.getLong(_columnIndexOfSelectedVariantIndex));
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
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
          _tmpMessage = new MessageEntity(_tmpMessageId,_tmpChatId,_tmpSender,_tmpContent,_tmpTimestamp,_tmpOrderIndex,_tmpRoleName,_tmpSelectedVariantIndex,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt,_tmpDisplayMode,_tmpIsFavorite);
          _result = new MessageContentRow(_tmpMessage,_tmpContentCharacterCount);
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
  protected Object queryMessageContentChunk(final long messageId, final long startCharacter,
      final int characterCount, final Continuation<? super String> $completion) {
    final String _sql = "SELECT SUBSTR(content, ?, ?) FROM messages WHERE messageId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startCharacter);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, characterCount);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, messageId);
        final String _result;
        if (_stmt.step()) {
          final String _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(0);
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
  protected Object queryVariantsForChat(final String chatId,
      final Continuation<? super List<MessageVariantContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        variantId,\n"
            + "        chatId,\n"
            + "        messageTimestamp,\n"
            + "        variantIndex,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        roleName,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM message_variants\n"
            + "     WHERE chatId = ? ORDER BY messageTimestamp ASC, variantIndex ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (chatId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, chatId);
        }
        final int _columnIndexOfVariantId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfMessageTimestamp = 2;
        final int _columnIndexOfVariantIndex = 3;
        final int _columnIndexOfContent = 4;
        final int _columnIndexOfRoleName = 5;
        final int _columnIndexOfProvider = 6;
        final int _columnIndexOfModelName = 7;
        final int _columnIndexOfInputTokens = 8;
        final int _columnIndexOfOutputTokens = 9;
        final int _columnIndexOfCachedInputTokens = 10;
        final int _columnIndexOfSentAt = 11;
        final int _columnIndexOfOutputDurationMs = 12;
        final int _columnIndexOfWaitDurationMs = 13;
        final int _columnIndexOfCompletedAt = 14;
        final int _columnIndexOfContentCharacterCount = 15;
        final List<MessageVariantContentRow> _result = new ArrayList<MessageVariantContentRow>();
        while (_stmt.step()) {
          final MessageVariantContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageVariantEntity _tmpVariant;
          final long _tmpVariantId;
          _tmpVariantId = _stmt.getLong(_columnIndexOfVariantId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpMessageTimestamp;
          _tmpMessageTimestamp = _stmt.getLong(_columnIndexOfMessageTimestamp);
          final int _tmpVariantIndex;
          _tmpVariantIndex = (int) (_stmt.getLong(_columnIndexOfVariantIndex));
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
          _tmpVariant = new MessageVariantEntity(_tmpVariantId,_tmpChatId,_tmpMessageTimestamp,_tmpVariantIndex,_tmpContent,_tmpRoleName,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt);
          _item = new MessageVariantContentRow(_tmpVariant,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryVariantsForMessageRange(final String chatId, final long minTimestamp,
      final long maxTimestamp,
      final Continuation<? super List<MessageVariantContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        variantId,\n"
            + "        chatId,\n"
            + "        messageTimestamp,\n"
            + "        variantIndex,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        roleName,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM message_variants\n"
            + "     WHERE chatId = ? AND messageTimestamp >= ? AND messageTimestamp <= ? ORDER BY messageTimestamp ASC, variantIndex ASC";
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
        _stmt.bindLong(_argIndex, minTimestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, maxTimestamp);
        final int _columnIndexOfVariantId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfMessageTimestamp = 2;
        final int _columnIndexOfVariantIndex = 3;
        final int _columnIndexOfContent = 4;
        final int _columnIndexOfRoleName = 5;
        final int _columnIndexOfProvider = 6;
        final int _columnIndexOfModelName = 7;
        final int _columnIndexOfInputTokens = 8;
        final int _columnIndexOfOutputTokens = 9;
        final int _columnIndexOfCachedInputTokens = 10;
        final int _columnIndexOfSentAt = 11;
        final int _columnIndexOfOutputDurationMs = 12;
        final int _columnIndexOfWaitDurationMs = 13;
        final int _columnIndexOfCompletedAt = 14;
        final int _columnIndexOfContentCharacterCount = 15;
        final List<MessageVariantContentRow> _result = new ArrayList<MessageVariantContentRow>();
        while (_stmt.step()) {
          final MessageVariantContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageVariantEntity _tmpVariant;
          final long _tmpVariantId;
          _tmpVariantId = _stmt.getLong(_columnIndexOfVariantId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpMessageTimestamp;
          _tmpMessageTimestamp = _stmt.getLong(_columnIndexOfMessageTimestamp);
          final int _tmpVariantIndex;
          _tmpVariantIndex = (int) (_stmt.getLong(_columnIndexOfVariantIndex));
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
          _tmpVariant = new MessageVariantEntity(_tmpVariantId,_tmpChatId,_tmpMessageTimestamp,_tmpVariantIndex,_tmpContent,_tmpRoleName,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt);
          _item = new MessageVariantContentRow(_tmpVariant,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryVariantsForMessage(final String chatId, final long messageTimestamp,
      final Continuation<? super List<MessageVariantContentRow>> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        variantId,\n"
            + "        chatId,\n"
            + "        messageTimestamp,\n"
            + "        variantIndex,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        roleName,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM message_variants\n"
            + "     WHERE chatId = ? AND messageTimestamp = ? ORDER BY variantIndex ASC";
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
        _stmt.bindLong(_argIndex, messageTimestamp);
        final int _columnIndexOfVariantId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfMessageTimestamp = 2;
        final int _columnIndexOfVariantIndex = 3;
        final int _columnIndexOfContent = 4;
        final int _columnIndexOfRoleName = 5;
        final int _columnIndexOfProvider = 6;
        final int _columnIndexOfModelName = 7;
        final int _columnIndexOfInputTokens = 8;
        final int _columnIndexOfOutputTokens = 9;
        final int _columnIndexOfCachedInputTokens = 10;
        final int _columnIndexOfSentAt = 11;
        final int _columnIndexOfOutputDurationMs = 12;
        final int _columnIndexOfWaitDurationMs = 13;
        final int _columnIndexOfCompletedAt = 14;
        final int _columnIndexOfContentCharacterCount = 15;
        final List<MessageVariantContentRow> _result = new ArrayList<MessageVariantContentRow>();
        while (_stmt.step()) {
          final MessageVariantContentRow _item;
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageVariantEntity _tmpVariant;
          final long _tmpVariantId;
          _tmpVariantId = _stmt.getLong(_columnIndexOfVariantId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpMessageTimestamp;
          _tmpMessageTimestamp = _stmt.getLong(_columnIndexOfMessageTimestamp);
          final int _tmpVariantIndex;
          _tmpVariantIndex = (int) (_stmt.getLong(_columnIndexOfVariantIndex));
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
          _tmpVariant = new MessageVariantEntity(_tmpVariantId,_tmpChatId,_tmpMessageTimestamp,_tmpVariantIndex,_tmpContent,_tmpRoleName,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt);
          _item = new MessageVariantContentRow(_tmpVariant,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  protected Object queryVariantForMessage(final String chatId, final long messageTimestamp,
      final int variantIndex, final Continuation<? super MessageVariantContentRow> $completion) {
    final String _sql = "\n"
            + "    SELECT\n"
            + "        variantId,\n"
            + "        chatId,\n"
            + "        messageTimestamp,\n"
            + "        variantIndex,\n"
            + "        SUBSTR(content, 1, 65536) AS content,\n"
            + "        roleName,\n"
            + "        provider,\n"
            + "        modelName,\n"
            + "        inputTokens,\n"
            + "        outputTokens,\n"
            + "        cachedInputTokens,\n"
            + "        sentAt,\n"
            + "        outputDurationMs,\n"
            + "        waitDurationMs,\n"
            + "        completedAt,\n"
            + "        LENGTH(content) AS contentCharacterCount\n"
            + "    FROM message_variants\n"
            + "     WHERE chatId = ? AND messageTimestamp = ? AND variantIndex = ? LIMIT 1";
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
        _stmt.bindLong(_argIndex, messageTimestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, variantIndex);
        final int _columnIndexOfVariantId = 0;
        final int _columnIndexOfChatId = 1;
        final int _columnIndexOfMessageTimestamp = 2;
        final int _columnIndexOfVariantIndex = 3;
        final int _columnIndexOfContent = 4;
        final int _columnIndexOfRoleName = 5;
        final int _columnIndexOfProvider = 6;
        final int _columnIndexOfModelName = 7;
        final int _columnIndexOfInputTokens = 8;
        final int _columnIndexOfOutputTokens = 9;
        final int _columnIndexOfCachedInputTokens = 10;
        final int _columnIndexOfSentAt = 11;
        final int _columnIndexOfOutputDurationMs = 12;
        final int _columnIndexOfWaitDurationMs = 13;
        final int _columnIndexOfCompletedAt = 14;
        final int _columnIndexOfContentCharacterCount = 15;
        final MessageVariantContentRow _result;
        if (_stmt.step()) {
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          final MessageVariantEntity _tmpVariant;
          final long _tmpVariantId;
          _tmpVariantId = _stmt.getLong(_columnIndexOfVariantId);
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpMessageTimestamp;
          _tmpMessageTimestamp = _stmt.getLong(_columnIndexOfMessageTimestamp);
          final int _tmpVariantIndex;
          _tmpVariantIndex = (int) (_stmt.getLong(_columnIndexOfVariantIndex));
          final String _tmpContent;
          if (_stmt.isNull(_columnIndexOfContent)) {
            _tmpContent = null;
          } else {
            _tmpContent = _stmt.getText(_columnIndexOfContent);
          }
          final String _tmpRoleName;
          if (_stmt.isNull(_columnIndexOfRoleName)) {
            _tmpRoleName = null;
          } else {
            _tmpRoleName = _stmt.getText(_columnIndexOfRoleName);
          }
          final String _tmpProvider;
          if (_stmt.isNull(_columnIndexOfProvider)) {
            _tmpProvider = null;
          } else {
            _tmpProvider = _stmt.getText(_columnIndexOfProvider);
          }
          final String _tmpModelName;
          if (_stmt.isNull(_columnIndexOfModelName)) {
            _tmpModelName = null;
          } else {
            _tmpModelName = _stmt.getText(_columnIndexOfModelName);
          }
          final long _tmpInputTokens;
          _tmpInputTokens = _stmt.getLong(_columnIndexOfInputTokens);
          final long _tmpOutputTokens;
          _tmpOutputTokens = _stmt.getLong(_columnIndexOfOutputTokens);
          final long _tmpCachedInputTokens;
          _tmpCachedInputTokens = _stmt.getLong(_columnIndexOfCachedInputTokens);
          final long _tmpSentAt;
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt);
          final long _tmpOutputDurationMs;
          _tmpOutputDurationMs = _stmt.getLong(_columnIndexOfOutputDurationMs);
          final long _tmpWaitDurationMs;
          _tmpWaitDurationMs = _stmt.getLong(_columnIndexOfWaitDurationMs);
          final long _tmpCompletedAt;
          _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt);
          _tmpVariant = new MessageVariantEntity(_tmpVariantId,_tmpChatId,_tmpMessageTimestamp,_tmpVariantIndex,_tmpContent,_tmpRoleName,_tmpProvider,_tmpModelName,_tmpInputTokens,_tmpOutputTokens,_tmpCachedInputTokens,_tmpSentAt,_tmpOutputDurationMs,_tmpWaitDurationMs,_tmpCompletedAt);
          _result = new MessageVariantContentRow(_tmpVariant,_tmpContentCharacterCount);
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
  protected Object queryMessageVariantContentChunk(final long variantId, final long startCharacter,
      final int characterCount, final Continuation<? super String> $completion) {
    final String _sql = "SELECT SUBSTR(content, ?, ?) FROM message_variants WHERE variantId = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startCharacter);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, characterCount);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, variantId);
        final String _result;
        if (_stmt.step()) {
          final String _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(0);
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
  public Object getSelectedContentCharacterCountsByChat(
      final Continuation<? super List<ChatContentCharacterCount>> $completion) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            chats.id AS chatId,\n"
            + "            COALESCE(\n"
            + "                SUM(\n"
            + "                    CASE\n"
            + "                        WHEN messages.selectedVariantIndex = 0 THEN LENGTH(messages.content)\n"
            + "                        ELSE LENGTH(selectedVariant.content)\n"
            + "                    END\n"
            + "                ),\n"
            + "                0\n"
            + "            ) AS contentCharacterCount\n"
            + "        FROM chats\n"
            + "        LEFT JOIN messages\n"
            + "            ON messages.chatId = chats.id\n"
            + "        LEFT JOIN message_variants AS selectedVariant\n"
            + "            ON selectedVariant.chatId = messages.chatId\n"
            + "            AND selectedVariant.messageTimestamp = messages.timestamp\n"
            + "            AND selectedVariant.variantIndex = messages.selectedVariantIndex\n"
            + "        GROUP BY chats.id\n"
            + "        ";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfChatId = 0;
        final int _columnIndexOfContentCharacterCount = 1;
        final List<ChatContentCharacterCount> _result = new ArrayList<ChatContentCharacterCount>();
        while (_stmt.step()) {
          final ChatContentCharacterCount _item;
          final String _tmpChatId;
          if (_stmt.isNull(_columnIndexOfChatId)) {
            _tmpChatId = null;
          } else {
            _tmpChatId = _stmt.getText(_columnIndexOfChatId);
          }
          final long _tmpContentCharacterCount;
          _tmpContentCharacterCount = _stmt.getLong(_columnIndexOfContentCharacterCount);
          _item = new ChatContentCharacterCount(_tmpChatId,_tmpContentCharacterCount);
          _result.add(_item);
        }
        return _result;
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
