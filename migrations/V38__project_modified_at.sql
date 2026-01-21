ALTER TABLE project
    ADD last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

UPDATE project
SET last_modified_at = COALESCE(
    (
        SELECT MAX(created_at)
        FROM company_survey
        WHERE company_survey.project_id = project.id
    ),
    last_modified_at
);
