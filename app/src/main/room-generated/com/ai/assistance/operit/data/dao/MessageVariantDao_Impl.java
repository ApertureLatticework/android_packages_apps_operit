package com.ai.assistance.operit.data.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import com.ai.assistance.operit.data.model.MessageVariantEntity;
import java.lang.Class;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class MessageVariantDao_Impl implements MessageVariantDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<MessageVariantEntity> __insertAdapterOfMessageVariantEntity;

  private final EntityDeleteOrUpdateAdapter<MessageVariantEntity> __updateAdapterOfMessageVariantEntity;

  public MessageVariantDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfMessageVariantEntity = new EntityInsertAdapter<MessageVariantEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `message_variants` (`variantId`,`chatId`,`messageTimestamp`,`variantIndex`,`content`,`roleName`,`provider`,`modelName`,`inputTokens`,`outputTokens`,`cachedInputTokens`,`sentAt`,`outputDurationMs`,`waitDurationMs`,`completedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MessageVariantEntity entity) {
        statement.bindLong(1, entity.getVariantId());
        if (entity.getChatId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getChatId());
        }
        statement.bindLong(3, entity.getMessageTimestamp());
        statement.bindLong(4, entity.getVariantIndex());
        if (entity.getContent() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getContent());
        }
        if (entity.getRoleName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getRoleName());
        }
        if (entity.getProvider() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getProvider());
        }
        if (entity.getModelName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getModelName());
        }
        statement.bindLong(9, entity.getInputTokens());
        statement.bindLong(10, entity.getOutputTokens());
        statement.bindLong(11, entity.getCachedInputTokens());
        statement.bindLong(12, entity.getSentAt());
        statement.bindLong(13, entity.getOutputDurationMs());
        statement.bindLong(14, entity.getWaitDurationMs());
        statement.bindLong(15, entity.getCompletedAt());
      }
    };
    this.__updateAdapterOfMessageVariantEntity = new EntityDeleteOrUpdateAdapter<MessageVariantEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `message_variants` SET `variantId` = ?,`chatId` = ?,`messageTimestamp` = ?,`variantIndex` = ?,`content` = ?,`roleName` = ?,`provider` = ?,`modelName` = ?,`inputTokens` = ?,`outputTokens` = ?,`cachedInputTokens` = ?,`sentAt` = ?,`outputDurationMs` = ?,`waitDurationMs` = ?,`completedAt` = ? WHERE `variantId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final MessageVariantEntity entity) {
        statement.bindLong(1, entity.getVariantId());
        if (entity.getChatId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getChatId());
        }
        statement.bindLong(3, entity.getMessageTimestamp());
        statement.bindLong(4, entity.getVariantIndex());
        if (entity.getContent() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getContent());
        }
        if (entity.getRoleName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getRoleName());
        }
        if (entity.getProvider() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getProvider());
        }
        if (entity.getModelName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getModelName());
        }
        statement.bindLong(9, entity.getInputTokens());
        statement.bindLong(10, entity.getOutputTokens());
        statement.bindLong(11, entity.getCachedInputTokens());
        statement.bindLong(12, entity.getSentAt());
        statement.bindLong(13, entity.getOutputDurationMs());
        statement.bindLong(14, entity.getWaitDurationMs());
        statement.bindLong(15, entity.getCompletedAt());
        statement.bindLong(16, entity.getVariantId());
      }
    };
  }

  @Override
  public Object insertVariant(final MessageVariantEntity variant,
      final Continuation<? super Long> $completion) {
    if (variant == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfMessageVariantEntity.insertAndReturnId(_connection, variant);
    }, $completion);
  }

  @Override
  public Object insertVariants(final List<MessageVariantEntity> variants,
      final Continuation<? super Unit> $completion) {
    if (variants == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfMessageVariantEntity.insert(_connection, variants);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object updateVariant(final MessageVariantEntity variant,
      final Continuation<? super Unit> $completion) {
    if (variant == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfMessageVariantEntity.handle(_connection, variant);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object copyVariantsToChat(final String sourceChatId, final String targetChatId,
      final Long upToTimestampInclusive, final Continuation<? super Unit> $completion) {
    final String _sql = "\n"
            + "        INSERT INTO message_variants (\n"
            + "            chatId,\n"
            + "            messageTimestamp,\n"
            + "            variantIndex,\n"
            + "            content,\n"
            + "            roleName,\n"
            + "            provider,\n"
            + "            modelName,\n"
            + "            inputTokens,\n"
            + "            outputTokens,\n"
            + "            cachedInputTokens,\n"
            + "            sentAt,\n"
            + "            outputDurationMs,\n"
            + "            waitDurationMs,\n"
            + "            completedAt\n"
            + "        )\n"
            + "        SELECT\n"
            + "            ?,\n"
            + "            messageTimestamp,\n"
            + "            variantIndex,\n"
            + "            content,\n"
            + "            roleName,\n"
            + "            provider,\n"
            + "            modelName,\n"
            + "            inputTokens,\n"
            + "            outputTokens,\n"
            + "            cachedInputTokens,\n"
            + "            sentAt,\n"
            + "            outputDurationMs,\n"
            + "            waitDurationMs,\n"
            + "            completedAt\n"
            + "        FROM message_variants\n"
            + "        WHERE chatId = ?\n"
            + "            AND (? IS NULL OR messageTimestamp <= ?)\n"
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
  public Object deleteVariant(final String chatId, final long messageTimestamp,
      final int variantIndex, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM message_variants WHERE chatId = ? AND messageTimestamp = ? AND variantIndex = ?";
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
        _stmt.bindLong(_argIndex, messageTimestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, variantIndex);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteVariantsForMessage(final String chatId, final long messageTimestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM message_variants WHERE chatId = ? AND messageTimestamp = ?";
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
        _stmt.bindLong(_argIndex, messageTimestamp);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteVariantsFrom(final String chatId, final long messageTimestamp,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM message_variants WHERE chatId = ? AND messageTimestamp >= ?";
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
        _stmt.bindLong(_argIndex, messageTimestamp);
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object deleteAllVariantsForChat(final String chatId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM message_variants WHERE chatId = ?";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
