package com.tiar.kas.data;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.lang.Override;
import java.lang.SuppressWarnings;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
final class AppDatabase_AutoMigration_4_5_Impl extends Migration {
  public AppDatabase_AutoMigration_4_5_Impl() {
    super(4, 5);
  }

  @Override
  public void migrate(@NonNull final SupportSQLiteDatabase db) {
    db.execSQL("CREATE TABLE IF NOT EXISTS `templates` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `amount` INTEGER NOT NULL, `accountId` INTEGER NOT NULL, `toAccountId` INTEGER, `categoryId` INTEGER, `note` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`toAccountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
    db.execSQL("CREATE INDEX IF NOT EXISTS `index_templates_accountId` ON `templates` (`accountId`)");
    db.execSQL("CREATE INDEX IF NOT EXISTS `index_templates_toAccountId` ON `templates` (`toAccountId`)");
    db.execSQL("CREATE INDEX IF NOT EXISTS `index_templates_categoryId` ON `templates` (`categoryId`)");
  }
}
