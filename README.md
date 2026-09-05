# CallBlocker & Premium Dialer 🍏🚫

CallBlocker não é apenas um bloqueador de chamadas poderoso; ele é um **Discador Android Premium** completo, focado em minimalismo, performance e usabilidade. Com um design limpo e fluido inspirado na estética da Apple (iOS), ele substitui o aplicativo de telefone nativo do seu celular para oferecer uma experiência superior.

## ✨ Principais Funcionalidades

### 📱 Discador Premium (Novo!)
- **Interface Apple-Style:** Tela de discagem elegante com feedback tátil (haptics) de luxo em cada tecla.
- **Formatação Inteligente:** Formatação automática de números no padrão brasileiro enquanto você digita.
- **Alerta Anti-Spam:** Detecta e avisa instantaneamente se o número digitado for um prefixo de telemarketing (ex: 0303) antes mesmo de ligar.
- **Atalhos Rápidos:** Pressione e segure a tecla de apagar para limpar tudo, ou segure a tecla `1` para ligar direto para sua Caixa Postal.

### 📞 Tela de Ligação Avançada (In-Call UI)
- **Sensor de Proximidade:** Gerenciamento nativo de energia (`WakeLock`) que apaga a tela quando o celular encosta no rosto, evitando toques acidentais.
- **Wake Up:** A tela acende automaticamente e sobrepõe o bloqueio do aparelho quando alguém te liga.
- **Gestão de Áudio:** Menu de roteamento de áudio profissional, permitindo alternar facilmente entre Fone de Ouvido, Viva-Voz e **Bluetooth**.
- **Notificação Persistente:** Se você minimizar o aplicativo durante a chamada, uma notificação inteligente permite que você retorne à tela preta de ligação com um clique.

### 🛡️ Proteção e Bloqueio Silencioso
- **Motor de Bloqueio:** Usa a API moderna `CallScreeningService` do Android para rejeitar chamadas de fundo sem que o celular sequer toque ou a tela acenda.
- **Modos de Proteção:**
  - **Bloquear Desconhecidos:** Apenas contatos salvos na sua agenda podem te ligar.
  - **Bloquear Tudo:** Modo foco absoluto.
  - **Lista Negra Personalizada:** Bloqueio pontual de números irritantes.
- **Banco de Dados Profissional:** Histórico persistente salvo no **Room Database** (offline e instantâneo).

## 🚀 Como Usar

1. Instale o aplicativo.
2. Defina-o como o **Aplicativo de Telefone / Discador Padrão** nas configurações do seu Android. Sem isso, a proteção silenciosa e a tela de ligação premium não funcionarão.
3. As permissões de contatos só são solicitadas quando você decide abrir o discador, respeitando a sua privacidade (Sem coleta de dados obscura na abertura do app).

## 🛠️ Tecnologias Utilizadas

- **Kotlin** (Linguagem Principal)
- **Jetpack Compose** (UI Responsiva e Fluida, Substituindo o XML Antigo)
- **Room Database / KSP** (Persistência de Dados)
- **Material 3 / Icons Extended** (Design System)
- **Telecom APIs Avançadas** (`InCallService`, `CallScreeningService`)
- **Gestão de Energia / Haptics** (`PowerManager`, `LocalHapticFeedback`)

## 📄 Licença

Este projeto é distribuído sob a licença **MIT**. Você é livre para usá-lo, modificá-lo e distribuí-lo. Veja o arquivo `LICENSE` para mais detalhes.
