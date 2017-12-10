package io.gomint;

import lombok.Getter;

public enum TitleType {

    TYPE_CLEAR( 0 ),
    TYPE_RESET( 1 ),
    TYPE_TITLE( 2 ),
    TYPE_SUBTITLE( 3 ),
    TYPE_ACTION_BAR( 4 ),
    TYPE_ANIMATION_TIMES( 5 );

    @Getter
    private final int id;

    TitleType( int id ) {
        this.id = id;
    }
}
