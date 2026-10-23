package com.mayconrob.coinflow.ui.viewmodel.enums

enum class TipoFiltroData {
    HOJE,           // Filtra o dia de hoje (sem considerar horário)
    ULTIMOS_7_DIAS, // Filtra os últimos 7 dias (sem considerar horário)
    PERIODO         // Filtra um período personalizado (Data Início a Data Fim, sem considerar horário)
}
