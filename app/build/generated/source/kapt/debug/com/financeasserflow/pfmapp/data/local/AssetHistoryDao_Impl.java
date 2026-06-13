package com.financeasserflow.pfmapp.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.financeasserflow.pfmapp.data.model.AssetHistoryEntity;
import com.financeasserflow.pfmapp.data.model.ChangeType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AssetHistoryDao_Impl implements AssetHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AssetHistoryEntity> __insertionAdapterOfAssetHistoryEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteByAssetId;

  public AssetHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAssetHistoryEntity = new EntityInsertionAdapter<AssetHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `asset_histories` (`id`,`assetId`,`changeType`,`previousAmount`,`newAmount`,`memo`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AssetHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAssetId());
        final String _tmp = __converters.fromChangeType(entity.getChangeType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getPreviousAmount() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getPreviousAmount());
        }
        if (entity.getNewAmount() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getNewAmount());
        }
        if (entity.getMemo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMemo());
        }
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteByAssetId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM asset_histories WHERE assetId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final AssetHistoryEntity history,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAssetHistoryEntity.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByAssetId(final long assetId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByAssetId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, assetId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByAssetId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AssetHistoryEntity>> observeHistories(final long assetId) {
    final String _sql = "SELECT * FROM asset_histories WHERE assetId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, assetId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"asset_histories"}, new Callable<List<AssetHistoryEntity>>() {
      @Override
      @NonNull
      public List<AssetHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAssetId = CursorUtil.getColumnIndexOrThrow(_cursor, "assetId");
          final int _cursorIndexOfChangeType = CursorUtil.getColumnIndexOrThrow(_cursor, "changeType");
          final int _cursorIndexOfPreviousAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "previousAmount");
          final int _cursorIndexOfNewAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "newAmount");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AssetHistoryEntity> _result = new ArrayList<AssetHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AssetHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAssetId;
            _tmpAssetId = _cursor.getLong(_cursorIndexOfAssetId);
            final ChangeType _tmpChangeType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfChangeType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfChangeType);
            }
            _tmpChangeType = __converters.toChangeType(_tmp);
            final Long _tmpPreviousAmount;
            if (_cursor.isNull(_cursorIndexOfPreviousAmount)) {
              _tmpPreviousAmount = null;
            } else {
              _tmpPreviousAmount = _cursor.getLong(_cursorIndexOfPreviousAmount);
            }
            final Long _tmpNewAmount;
            if (_cursor.isNull(_cursorIndexOfNewAmount)) {
              _tmpNewAmount = null;
            } else {
              _tmpNewAmount = _cursor.getLong(_cursorIndexOfNewAmount);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AssetHistoryEntity(_tmpId,_tmpAssetId,_tmpChangeType,_tmpPreviousAmount,_tmpNewAmount,_tmpMemo,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
