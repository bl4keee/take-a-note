package com.bwalczak.note.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    HIGH (3),
    MEDIUM (2),
    LOW (1);

    private final int level;
}
