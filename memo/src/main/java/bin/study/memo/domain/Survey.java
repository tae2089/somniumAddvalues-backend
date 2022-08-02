package bin.study.memo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Survey {
    private String item_name;
    private List<String> results;

    @Builder
    public Survey(String item_name, List<String> results) {
        this.item_name = item_name;
        this.results = results;
    }
}
