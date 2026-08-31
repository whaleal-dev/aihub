import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';
import generatedRedirects from './redirects.generated.json';

/** GitHub Pages 默认地址：https://whaleal-dev.github.io/aihub/ */
const siteUrl = process.env.DOCS_SITE_URL ?? 'https://whaleal-dev.github.io';
const siteBaseUrl = process.env.DOCS_SITE_BASE_URL ?? '/aihub/';

const config: Config = {
  title: 'AIHub 文档站',
  tagline: '面向 JDK 8+ 的 Java 大模型客户端',
  favicon: 'img/favicon.ico',
  future: {v4: true},
  url: siteUrl,
  baseUrl: siteBaseUrl,
  organizationName: 'whaleal-dev',
  projectName: 'aihub',
  onBrokenLinks: 'throw',
  i18n: {
    defaultLocale: 'zh-Hans',
    locales: ['zh-Hans', 'en'],
  },
  presets: [
    [
      'classic',
      {
        docs: {
          routeBasePath: 'docs',
          sidebarPath: './sidebars.ts',
          editUrl: 'https://github.com/whaleal-dev/aihub/tree/main/docs-site/',
        },
        blog: false,
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],
  plugins: [
    [
      '@docusaurus/plugin-client-redirects',
      {
        redirects: generatedRedirects,
      },
    ],
  ],
  themes: [
    [
      require.resolve('@easyops-cn/docusaurus-search-local'),
      {
        hashed: true,
        language: ['zh', 'en'],
        indexDocs: true,
        indexBlog: false,
        docsRouteBasePath: 'docs',
        searchResultLimits: 10,
        searchResultContextMaxLength: 80,
      },
    ],
  ],
  themeConfig: {
    image: 'img/docusaurus-social-card.jpg',
    navbar: {
      title: 'AIHub 文档站',
      logo: {
        alt: 'AIHub Logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: '文档',
        },
        {
          type: 'search',
          position: 'right',
        },
        {
          type: 'localeDropdown',
          position: 'right',
        },
        {
          href: 'https://github.com/whaleal-dev/aihub',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: '文档',
          items: [
            {label: '开始阅读', to: '/docs/intro'},
            {label: '模型接入', to: '/docs/capabilities/models/overview'},
            {label: 'Spring Boot', to: '/docs/integrations/spring-boot/overview'},
          ],
        },
        {
          title: '资源',
          items: [
            {label: 'GitHub', href: 'https://github.com/whaleal-dev/aihub'},
            {label: 'Issues', href: 'https://github.com/whaleal-dev/aihub/issues'},
            {label: '贡献指南', to: '/docs/reference/about/contributing'},
          ],
        },
      ],
      copyright: `Copyright (c) ${new Date().getFullYear()} whaleal-dev · 基于 Docusaurus 构建`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
