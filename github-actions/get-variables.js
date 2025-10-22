/**
 * @typedef BuildVariables
 * @type {object}
 * @property {string} ZTOR_PR_CONTAINER_APP_NAME - Azure Container Apps name for Pull Request environment
 * @property {string} VERSION_TAG - tag name for containers, libraries, etc
 */

/**
 * Called by @actions/github-script to get the variables for the build.
 *
 * @param {import('@actions/github/lib/context').Context} context
 * @param {object} env
 *
 * @returns {BuildVariables}
 */
module.exports = (context) => {
    const commit = context.payload.pull_request?.head?.sha ?? context.sha
    const shortCommit = commit.substring(0, 7)

    const containerAppBaseName = 'ztor'
    const branch = context.payload.pull_request?.head?.ref ?? context.ref.match(/refs\/heads\/(.+)/)[1]
    // Azure Container Apps name can be max 32 characters
    const maxBranchLength = 32 - `${context.runNumber}`.length - containerAppBaseName.length - 2 * '-'.length
    const shortBranch = branch
        .toLowerCase()
        .substring(0, maxBranchLength)
        .replaceAll(/-*$/g, '') // remove trailing dashes because it would lead to an invalid name

    const versionIdentifier = `${shortBranch}-${context.runNumber}`
    const environment = getEnvironment(context)

    const baseVars = {
        ZTOR_PR_CONTAINER_APP_NAME:  `${containerAppBaseName}-${versionIdentifier}`,
        VERSION_TAG: `${versionIdentifier}-${shortCommit}`,
        DOCKER_STACK_NAME: `zero-${environment}`,
        GITHUB_ENVIRONMENT: `swarm-${environment}`,
        AZURE_STORAGE_ACCOUNT_NAME: "zerostore",
        // AZURE_STORAGE_ACCOUNT_KEY is a GitHub repository secret
        // TODO: go to a per-environment Minio access key when we see that the feature is used
        MINIO_ACCESS_KEY: "admin",
        // MINIO_SECRET_KEY is a GitHub repository secret
    }

    const environmentSpecificVars = {
        production: {
            FRONTEND_HOSTNAME: "zero.zenmo.com",
            ZTOR_HOSTNAME: "ztor.zero.zenmo.com",
            DB_NAME: "zero_production",
            // POSTGRES_PASSWORD is a GitHub environment secret
            AZURE_STORAGE_CONTAINER: "prod",
            MINIO_ER_EXCEL_UPLOAD_BUCKET: "energieke-regio-excel-uploads-production",
            CORS_ALLOW_ORIGIN_PATTERN: "https://zero.zenmo.com",
            OAUTH_CLIENT_ID: "zero-configurator-prod",
            // OAUTH_CLIENT_SECRET is a GitHub environment secret
        },
        test: {
            FRONTEND_HOSTNAME: "zero-test.zenmo.com",
            ZTOR_HOSTNAME: "ztor-test.zero.zenmo.com",
            DB_NAME: "zero_test",
            // POSTGRES_PASSWORD is a GitHub environment secret
            AZURE_STORAGE_CONTAINER: "test",
            MINIO_ER_EXCEL_UPLOAD_BUCKET: "energieke-regio-excel-uploads-test",
            CORS_ALLOW_ORIGIN_PATTERN: "https://zero-test.zenmo.com",
            OAUTH_CLIENT_ID: "zero-configurator-test",
            // OAUTH_CLIENT_SECRET is a GitHub environment secret
        },
        pullrequest: {
            FRONTEND_HOSTNAME: `frontend-${versionIdentifier}.zero.zenmo.com`,
            ZTOR_HOSTNAME: `ztor-${versionIdentifier}.zero.zenmo.com`,
            DB_NAME: "zero_pullrequest",
            // POSTGRES_PASSWORD is a GitHub environment secret
            AZURE_STORAGE_CONTAINER: "dev",
            MINIO_ER_EXCEL_UPLOAD_BUCKET: "energieke-regio-excel-uploads-pullrequest",
            CORS_ALLOW_ORIGIN_PATTERN: "https://frontend-[\\\\w-]+.zero.zenmo.com",
            OAUTH_CLIENT_ID: "zero-configurator-test",
            // OAUTH_CLIENT_SECRET is a GitHub environment secret
        },
    }

    return {
        ...baseVars,
        ...environmentSpecificVars[environment],
    }
}

const branchToEnvironment = {
    production: 'production',
    main: 'test',
}

/**
 * Get environment name which is used for variable and secret management in GitHub Actions.
 *
 * @param {import('@actions/github/lib/context').Context} context
 *
 * @returns {'production' | 'test' | 'pullrequest'}
 */
function getEnvironment(context) {
    if (context.payload.pull_request) {
        return 'pullrequest'
    }

    const branch = context.ref.match(/refs\/heads\/(.+)/)[1]

    return branchToEnvironment[branch] ?? 'pullrequest'
}
