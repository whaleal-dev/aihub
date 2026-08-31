import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'JDK 8 可落地',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        面向存量 Java 系统：同一套 Chat / Embedding / 媒体客户端，不要求先升级 JDK。
      </>
    ),
  },
  {
    title: '多厂商同一套调用',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        OpenAI、通义、DeepSeek、Anthropic 等走 <code>AiService</code> 工厂，同步与 SSE 都在客户端里解析。
      </>
    ),
  },
  {
    title: 'Spring Boot 可选',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        普通 Java 直接 new 客户端；Spring 项目再加 starter。不做 RAG、MCP 或 Agent 运行时。
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
