package edu.demo.scfs.config;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LocalSchemaRepair {
    @Bean
    @Order(0)
    CommandLineRunner repairH2Schema(DataSource dataSource, JdbcTemplate jdbc) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                String url = connection.getMetaData().getURL();
                if (url == null || !url.startsWith("jdbc:h2:")) {
                    return;
                }
            }
            for (String statement : statements()) {
                jdbc.execute(statement);
            }
        };
    }

    private String[] statements() {
        return new String[] {
                "ALTER TABLE categories ADD COLUMN IF NOT EXISTS default_sla_hours INTEGER DEFAULT 72 NOT NULL",
                "ALTER TABLE categories ADD COLUMN IF NOT EXISTS workflow_template VARCHAR(40) DEFAULT 'STANDARD' NOT NULL",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS tracking_code_hash VARCHAR(128)",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS public_contact_email VARCHAR(160)",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS public_submitter_type VARCHAR(60)",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS priority VARCHAR(30) DEFAULT 'NORMAL' NOT NULL",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS workflow_template VARCHAR(40) DEFAULT 'STANDARD' NOT NULL",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS due_at TIMESTAMP",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS overdue BOOLEAN DEFAULT FALSE NOT NULL",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS escalated_at TIMESTAMP",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS reopened_at TIMESTAMP",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS satisfaction_rating INTEGER",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS satisfaction_comment VARCHAR(1200)",
                "ALTER TABLE cases ADD COLUMN IF NOT EXISTS satisfaction_at TIMESTAMP",
                "ALTER TABLE case_attachments ADD COLUMN IF NOT EXISTS virus_scan_status VARCHAR(40) DEFAULT 'NOT_SCANNED' NOT NULL",
                "ALTER TABLE notifications ADD COLUMN IF NOT EXISTS failure_reason VARCHAR(1000)",
                "ALTER TABLE notifications ADD COLUMN IF NOT EXISTS case_id BIGINT",
                "ALTER TABLE notifications ADD COLUMN IF NOT EXISTS case_number VARCHAR(80)",
                "ALTER TABLE notifications ADD COLUMN IF NOT EXISTS action_type VARCHAR(80)",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT TRUE NOT NULL",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_token_hash VARCHAR(128)",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token_hash VARCHAR(128)",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_token_expires_at TIMESTAMP"
        };
    }
}
