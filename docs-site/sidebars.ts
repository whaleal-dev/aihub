import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    'intro',
    {
      type: 'category',
      label: '入门',
      items: [
        'getting-started/why-aihub',
        'getting-started/quickstart-java',
        'getting-started/quickstart-spring-boot',
        'getting-started/first-tool-call',
        'getting-started/programmatic-integration',
      ],
    },
    {
      type: 'category',
      label: '模型接入',
      items: [
        'capabilities/models/overview',
        'capabilities/models/chat',
        'capabilities/models/responses',
        'capabilities/models/chat-vs-responses',
        'capabilities/models/messages',
        'capabilities/models/streaming',
        'capabilities/models/multimodal',
        'capabilities/models/request-and-response-conventions',
        'capabilities/models/openai-compatible-and-trovebox',
        'capabilities/models/platform-service-matrix',
      ],
    },
    {
      type: 'category',
      label: '媒体生成',
      items: [
        'capabilities/media/image-generation',
        'capabilities/media/audio',
        'capabilities/media/realtime',
        'capabilities/media/video-generation',
        'capabilities/media/music-generation',
      ],
    },
    {
      type: 'category',
      label: '工具协议',
      items: [
        'capabilities/tools/overview',
        'capabilities/tools/function-calling',
        'capabilities/tools/annotation-based-tools',
      ],
    },
    {
      type: 'category',
      label: 'Spring Boot',
      items: [
        'integrations/spring-boot/overview',
        'integrations/spring-boot/quickstart',
        'integrations/spring-boot/auto-configuration',
        'integrations/spring-boot/configuration-reference',
      ],
    },
    {
      type: 'category',
      label: '参考',
      items: [
        'reference/faq',
        'reference/about/contributing',
        'reference/maintainers/github-pages',
      ],
    },
  ],
};

export default sidebars;
