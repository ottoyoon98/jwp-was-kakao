package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileDirectory {
    TEMPLATES("./templates"),
    STATIC("./static"),
    ;

    private final String directory;
}
