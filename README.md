# CoinFlow

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API_26+-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)

O **CoinFlow** é um aplicativo Android nativo desenvolvido para ajudar no controle e organização financeira pessoal. Construído totalmente em **Kotlin** com as tecnologias mais modernas do ecossistema Android, ele utiliza **Jetpack Compose** para a interface declarativa e **Room Database** para o armazenamento local persistente, seguro e privativo.

---

## ✨ Funcionalidades

- 📊 **Gestão de Transações:** Permite cadastrar, editar e excluir entrada e saída financeira de forma simples.
- 🗂️ **Categorização Inteligente:** Organização de gastos por categorias personalizáveis.
- 📈 **Painel de Controle (Dashboard):** Saldo total acumulado, entradas e saídas consolidadas por mês/ano selecionável.
- 📜 **Extrato Financeiro Completo:** Histórico detalhado com pesquisa e filtros avançados.
- 💾 **Armazenamento 100% Local:** Seus dados financeiros nunca saem do seu dispositivo.
- 🎨 **Material 3 Design:** Interface moderna, responsiva e integrada ao Modo Escuro (Dark Theme).

---

## 🛠️ Tecnologias e Arquitetura

O projeto foi estruturado seguindo as melhores práticas recomendadas pela Google:

- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) com [Material 3](https://m3.material.io/)
- **Arquitetura:** MVVM (Model-View-ViewModel) + Clean Architecture (Separação entre Domain, Models, Mappers e Room Entities)
- **Persistência de Dados:** [Room Database](https://developer.android.com/training/data-storage/room)
- **Assincronismo:** Kotlin Coroutines & StateFlow
- **Injeção de Dependência:** Hilt (Dagger)
- **Gerenciador de Build:** Gradle (Kotlin DSL)

---

## 🚀 Melhoria Contínua & Roadmap

O **CoinFlow** está em constante evolução arquitetural e funcional. As próximas etapas planejadas para a melhoria contínua do projeto incluem:

- [ ] **Testes Automatizados:** Implementação de suítes de testes unitários (JUnit / MockK) para ViewModels e Use Cases, além de testes de UI com Jetpack Compose.
- [ ] **Exportação de Dados:** Funcionalidade para exportar e importar dados financeiros em formatos como CSV ou JSON.
- [ ] **Gráficos e Relatórios Avançados:** Inclusão de gráficos interativos de pizza/barra para análise detalhada de gastos por categoria e histórico mensal.
- [ ] **Internacionalização (i18n):** Suporte a múltiplos idiomas (Português, Inglês e Espanhol).
- [ ] **Segurança Adicional:** Suporte opcional a autenticação biométrica (BiometricPrompt) para acesso ao aplicativo.

---

## 🤖 Uso de Inteligência Artificial

A interface gráfica do **CoinFlow** (desenvolvida em **Jetpack Compose** com Material 3) foi desenhada e estruturada com o auxílio de **Inteligência Artificial**, otimizando o fluxo de prototipagem e a construção dos componentes visuais da aplicação.

---

## 📄 Licença

Este projeto é um software livre distribuído sob os termos da licença **GNU General Public License v3.0 (GPLv3)**. Para mais detalhes, consulte o arquivo [LICENSE](LICENSE).

---

Desenvolvido com 💙 por **[Maycon Roberto](https://github.com/mayconrob)**
