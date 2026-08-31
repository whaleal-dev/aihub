import type {ReactNode} from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import Heading from '@theme/Heading';

import styles from './index.module.css';

const quickRoutes = [
  {
    title: '开始调用模型',
    description: '从 Maven 依赖、配置和第一段 Java 调用开始。',
    to: '/docs/getting-started/quickstart-java',
  },
  {
    title: 'Spring Boot 接入',
    description: '用 starter 把客户端注入 Spring。',
    to: '/docs/getting-started/quickstart-spring-boot',
  },
  {
    title: '工具协议字段',
    description: '请求里带 tools，响应里解析 tool_calls。',
    to: '/docs/capabilities/tools/overview',
  },
  {
    title: '查 Java API',
    description: '打开已发布模块的 Javadoc。',
    to: '/docs/reference/api',
  },
];

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
          {siteConfig.title}
        </Heading>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link className="button button--secondary button--lg" to="/docs/intro">
            开始阅读文档
          </Link>
          <Link className="button button--info button--lg margin-left--md" to="/docs/getting-started/quickstart-java">
            Java 快速开始
          </Link>
        </div>
      </div>
    </header>
  );
}

function QuickNavigation() {
  return (
    <section className={styles.quickRoutes} aria-labelledby="quick-routes-heading">
      <div className="container">
        <Heading as="h2" id="quick-routes-heading">按任务进入</Heading>
        <p className={styles.quickRoutesIntro}>
          aihub 只做 Java 大模型客户端。从一个入口开始即可。
        </p>
        <div className={styles.quickRouteGrid}>
          {quickRoutes.map((route) => (
            <Link className={styles.quickRoute} key={route.to} to={route.to}>
              <Heading as="h3">{route.title}</Heading>
              <p>{route.description}</p>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}

export default function Home(): ReactNode {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={`${siteConfig.title}`}
      description="aihub 是面向 JDK 8+ 的 Java 大模型客户端，只封装厂商 HTTP / SSE API。">
      <HomepageHeader />
      <main>
        <QuickNavigation />
        <HomepageFeatures />
      </main>
    </Layout>
  );
}
