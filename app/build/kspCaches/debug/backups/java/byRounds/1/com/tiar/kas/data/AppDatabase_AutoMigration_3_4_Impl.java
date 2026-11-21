package com.tiar.kas.data;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.lang.Override;
import java.lang.SuppressWarnings;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
final class AppDatabase_AutoMigration_3_4_Impl extends Migration {
  public AppDatabase_AutoMigration_3_4_Impl() {
    super(3, 4);
  }

  @Override
  public void migrate(@NonNull final SupportSQLiteDatabase db) {
    db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`categoryId` INTEGER NOT NULL, `amount` INTEGER NOT NULL, PRIMARY KEY(`categoryId`), FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
  }
}
