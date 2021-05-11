package liquibase.extension.testing.command

import liquibase.exception.CommandValidationException

CommandTests.define {
    command = ["futureRollbackSql"]
    signature = """
Short Description: Generate the raw SQL needed to rollback undeployed changes
Long Description: NOT SET
Required Args:
  changelogFile (String) The root changelog
  url (String) The JDBC Database connection URL
Optional Args:
  contexts (String) Changeset contexts to match
    Default: null
  defaultCatalogName (String) The default catalog name to use for the database connection
    Default: null
  defaultSchemaName (String) The default schema name to use for the database connection
    Default: null
  labels (String) Changeset labels to match
    Default: null
  password (String) Password to use to connect to the database
    Default: null
    OBFUSCATED
  username (String) Username to use to connect to the database
    Default: null
"""

    run "Happy path", {
        arguments = [
                changelogFile: "changelogs/hsqldb/complete/rollback.changelog.xml",
        ]

        setup {
            runChangelog "changelogs/hsqldb/complete/rollback.changelog.xml"
            rollback 5, "changelogs/hsqldb/complete/rollback.changelog.xml"

        }

        expectedResults = [
                statusCode   : 0
        ]
    }

    run "Happy path with an output file", {
        arguments = [
                changelogFile: "changelogs/hsqldb/complete/rollback.changelog.xml",
        ]

        setup {
            cleanResources("target/test-classes/futureRollback.sql")
            runChangelog "changelogs/hsqldb/complete/rollback.changelog.xml"
            rollback 5, "changelogs/hsqldb/complete/rollback.changelog.xml"

        }

        outputFile = new File("target/test-classes/futureRollback.sql")

        expectedFileContent = [
                //
                // Find the " -- Release Database Lock" line
                //
                "target/test-classes/futureRollback.sql" : [CommandTests.assertContains("-- Release Database Lock")]
        ]

        expectedResults = [
                statusCode   : 0
        ]
    }

    run "Run without any arguments should throw an exception",  {
        arguments = [
                url: ""
        ]
        expectedException = CommandValidationException.class
    }

    run "Run without a changeLogFile should throw an exception",  {
        arguments = [
                url: "",
                changelogFile: ""
        ]
        expectedException = CommandValidationException.class
    }

    run "Run without a URL should throw an exception",  {
        arguments = [
                url          : "",
                changelogFile: "changelogs/hsqldb/complete/rollback.tag.changelog.xml",
        ]
        expectedException = CommandValidationException.class
    }
}
