package model

import (
	"blog/utils"
	"fmt"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
	"gorm.io/gorm/schema"
	"time"
)

var db *gorm.DB
var err error

func init() {
	dns := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?charset=utf8mb4&parseTime=True&loc=Local",
		utils.DbUser,
		utils.DbPassWord,
		utils.DbHost,
		utils.DbPort,
		utils.DbName,
	)
	db, err = gorm.Open(mysql.Open(dns), &gorm.Config{
		// gorm日志模式：silent
		Logger: logger.Default.LogMode(logger.Silent),
		// 外键约束
		DisableForeignKeyConstraintWhenMigrating: false,
		// 禁用默认事务（提高运行速度）
		SkipDefaultTransaction: true,
		NamingStrategy: schema.NamingStrategy{
			// 使用单数表名，启用该选项，此时，`User` 的表名应该是 `user`
			SingularTable: true,
		},
	})
	sqlDB, _ := db.DB()
	// SetMaxIdleCons 设置连接池中的最大闲置连接数。
	sqlDB.SetMaxIdleConns(10)

	// SetMaxOpenCons 设置数据库的最大连接数量。
	sqlDB.SetMaxOpenConns(100)

	// SetConnMaxLifetiment 设置连接的最大可复用时间。
	sqlDB.SetConnMaxLifetime(10 * time.Second)
}
