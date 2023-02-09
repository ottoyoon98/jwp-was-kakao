package webapplication.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Login {
    private String userId;
    private String password;
}
