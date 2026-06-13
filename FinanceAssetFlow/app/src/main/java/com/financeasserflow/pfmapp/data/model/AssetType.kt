package com.financeasserflow.pfmapp.data.model

enum class AssetType {
    ASSET,
    LIABILITY,

    ;

    val label: String
        get() = when (this) {
            ASSET -> "자산"
            LIABILITY -> "부채"
        }
}

enum class AssetCategory {
    CASH,
    SAVINGS,
    INVESTMENT,
    LIABILITY,
    ETC,

    ;

    val label: String
        get() = when (this) {
            CASH -> "현금성"
            SAVINGS -> "예적금"
            INVESTMENT -> "투자자산"
            LIABILITY -> "부채"
            ETC -> "기타"
        }
}

enum class ChangeType {
    CREATED,
    UPDATED,
    DELETED,
    VALUATION_UPDATED,

    ;

    val label: String
        get() = when (this) {
            CREATED -> "생성"
            UPDATED -> "수정"
            DELETED -> "삭제"
            VALUATION_UPDATED -> "평가금액 수정"
        }
}

