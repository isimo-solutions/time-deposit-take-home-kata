DECLARE
    name_already_used EXCEPTION;
    PRAGMA EXCEPTION_INIT(name_already_used, -955);

    -- Oracle XE 21c uses this block to make table creation safe on restart.
    PROCEDURE create_table_if_absent(ddl IN VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE ddl;
    EXCEPTION
        WHEN name_already_used THEN NULL;
    END;
BEGIN
    create_table_if_absent('
        CREATE TABLE timeDeposits (
            id INTEGER PRIMARY KEY,
            planType VARCHAR2(255) NOT NULL,
            days INTEGER NOT NULL,
            balance DECIMAL(38, 2) NOT NULL
        )');

    create_table_if_absent('
        CREATE TABLE withdrawals (
            id INTEGER PRIMARY KEY,
            timeDepositId INTEGER NOT NULL REFERENCES timeDeposits(id),
            amount DECIMAL(38, 2) NOT NULL,
            "DATE" DATE NOT NULL
        )');
END;
/
