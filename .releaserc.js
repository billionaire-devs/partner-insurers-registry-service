const sanitizedNameTemplate = (fallback) =>
    `\${((name || '${fallback}').replace(/[^0-9A-Za-z-]/g, '-').replace(/-+/g, '-').replace(/^-+|-+$/g, '') || '${fallback}')}`;

const changelogTypes = [
    {type: 'feat', section: '🚀 Features'},
    {type: 'fix', section: '🐛 Bug Fixes'},
    {type: 'perf', section: '⚡ Performance Improvements'},
    {type: 'refactor', section: '♻️ Code Refactoring'},
    {type: 'docs', section: '📝 Documentation'},
    {type: 'build', section: '🏗️ Build System'},
    {type: 'ci', section: '👷 CI/CD'},
    {type: 'test', section: '✅ Tests'},
    {type: 'revert', section: '⏪ Reverts'},
    {type: 'chore', section: '🔧 Chores'},
    // Hidden types - won't appear in changelog
    {type: 'style', hidden: true},
    {type: 'wip', hidden: true},
];

const wildcardBranch = (prefix, channel, fallback) => ({
    name: `${prefix}/*`,
    ...(channel ? {channel} : {}),
    prerelease: sanitizedNameTemplate(fallback),
});

module.exports = {
    repositoryUrl: 'https://github.com/billionaire-devs/partner-insurers-registry-service.git',

    branches: [
        'main',
        {name: 'develop', prerelease: 'beta'},
        wildcardBranch('feature', 'alpha', 'feature'),
        wildcardBranch('fix', 'bugfix', 'fix'),
        wildcardBranch('hotfix', 'patch', 'hotfix'),
        wildcardBranch('release', 'rc', 'release'),
    ],

    plugins: [
        [
            '@semantic-release/commit-analyzer',
            {
                preset: 'conventionalcommits',
                presetConfig: {
                    issuePrefixes: ['PIS-', 'COR-', 'BUG-', '#'],
                },
                releaseRules: [
                    {type: 'docs', scope: 'README', release: 'patch'},
                    {type: 'refactor', release: 'patch'},
                    {type: 'style', release: false},
                    {type: 'chore', scope: 'deps', release: 'patch'},
                    {scope: 'no-release', release: false},
                ],
                parserOpts: {
                    noteKeywords: ['BREAKING CHANGE', 'BREAKING CHANGES', 'BREAKING'],
                },
            },
        ],
        [
            '@semantic-release/release-notes-generator',
            {
                preset: 'conventionalcommits',
                presetConfig: {
                    types: changelogTypes,
                },
                writerOpts: {
                    commitGroupsSort: 'title',
                    commitsSort: ['scope', 'subject'],
                },
            },
        ],
        'gradle-semantic-release-plugin',
        [
            '@semantic-release/changelog',
            {
                changelogFile: 'CHANGELOG.md',
                changelogTitle: '# Changelog\n\nAll notable changes to this project will be documented in this file.',
            },
        ],
        [
            '@semantic-release/git',
            {
                assets: ['CHANGELOG.md', 'gradle.properties'],
                message: 'chore(release): ${nextRelease.version} [skip ci]\n\n${nextRelease.notes}',
            },
        ],
        [
            '@semantic-release/github',
            {
                assets: [
                    {
                        path: 'build/libs/*-${nextRelease.version}.jar',
                        label: 'Application JAR (${nextRelease.version})',
                    },
                    {
                        path: 'build/libs/*-${nextRelease.version}-plain.jar',
                        label: 'Plain JAR (${nextRelease.version})',
                    }
                ],
                addReleases: 'bottom',
                failComment: false,
                failTitle: false,
            },
        ],
        [
            '@semantic-release/exec',
            {
                prepareCmd: './gradlew clean build -x test',
                successCmd: `
                    echo "🎉 Release \${nextRelease.version} published successfully!"
                    echo "Repository: ${module.exports.repositoryUrl}"
                    echo "Version: \${nextRelease.version}"
                    echo "Channel: \${nextRelease.channel || 'latest'}"
                    echo "Release Notes:"
                    echo "\${nextRelease.notes}"
                    
                    if echo "\${nextRelease.version}" | grep -qE "^[0-9]+\\.0\\.0"; then
                        echo "Major release detected, consider creating announcement issue"
                    fi
                `,
                failCmd: `
                    echo "❌ Release failed!"
                    echo "Version: \${nextRelease.version}"
                    echo "Error: Release process encountered an error"
                `,
            },
        ],
    ],
};
