package com.tiar.kas.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
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
public final class TemplateDao_Impl implements TemplateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionTemplate> __insertionAdapterOfTransactionTemplate;

  private final EntityDeletionOrUpdateAdapter<TransactionTemplate> __updateAdapterOfTransactionTemplate;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfRename;

  public TemplateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransactionTemplate = new EntityInsertionAdapter<TransactionTemplate>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `templates` (`id`,`name`,`type`,`amount`,`accountId`,`toAccountId`,`categoryId`,`note`,`sortOrder`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionTemplate entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, __TransactionType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getAmount());
        statement.bindLong(5, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCategoryId());
        }
        statement.bindString(8, entity.getNote());
        statement.bindLong(9, entity.getSortOrder());
      }
    };
    this.__updateAdapterOfTransactionTemplate = new EntityDeletionOrUpdateAdapter<TransactionTemplate>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `templates` SET `id` = ?,`name` = ?,`type` = ?,`amount` = ?,`accountId` = ?,`toAccountId` = ?,`categoryId` = ?,`note` = ?,`sortOrder` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionTemplate entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, __TransactionType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getAmount());
        statement.bindLong(5, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCategoryId());
        }
        statement.bindString(8, entity.getNote());
        statement.bindLong(9, entity.getSortOrder());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM templates WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRename = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE templates SET name = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final TransactionTemplate template,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTransactionTemplate.insertAndReturnId(template);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TransactionTemplate template,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTransactionTemplate.handle(template);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object rename(final long id, final String name,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRename.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, name);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfRename.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TransactionTemplate>> observeAll() {
    final String _sql = "SELECT id, name, type, amount, accountId, toAccountId, categoryId, note, sortOrder FROM templates ORDER BY sortOrder, id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"templates"}, new Callable<List<TransactionTemplate>>() {
      @Override
      @NonNull
      public List<TransactionTemplate> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfAmount = 3;
          final int _cursorIndexOfAccountId = 4;
          final int _cursorIndexOfToAccountId = 5;
          final int _cursorIndexOfCategoryId = 6;
          final int _cursorIndexOfNote = 7;
          final int _cursorIndexOfSortOrder = 8;
          final List<TransactionTemplate> _result = new ArrayList<TransactionTemplate>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionTemplate _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final Long _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getLong(_cursorIndexOfToAccountId);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new TransactionTemplate(_tmpId,_tmpName,_tmpType,_tmpAmount,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpNote,_tmpSortOrder);
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

  @Override
  public Object getById(final long id,
      final Continuation<? super TransactionTemplate> $completion) {
    final String _sql = "SELECT id, name, type, amount, accountId, toAccountId, categoryId, note, sortOrder FROM templates WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionTemplate>() {
      @Override
      @Nullable
      public TransactionTemplate call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfAmount = 3;
          final int _cursorIndexOfAccountId = 4;
          final int _cursorIndexOfToAccountId = 5;
          final int _cursorIndexOfCategoryId = 6;
          final int _cursorIndexOfNote = 7;
          final int _cursorIndexOfSortOrder = 8;
          final TransactionTemplate _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final Long _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getLong(_cursorIndexOfToAccountId);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _result = new TransactionTemplate(_tmpId,_tmpName,_tmpType,_tmpAmount,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpNote,_tmpSortOrder);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object nextSortOrder(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM templates";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __TransactionType_enumToString(@NonNull final TransactionType _value) {
    switch (_value) {
      case PEMASUKAN: return "PEMASUKAN";
      case PENGELUARAN: return "PENGELUARAN";
      case TRANSFER: return "TRANSFER";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private TransactionType __TransactionType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PEMASUKAN": return TransactionType.PEMASUKAN;
      case "PENGELUARAN": return TransactionType.PENGELUARAN;
      case "TRANSFER": return TransactionType.TRANSFER;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
