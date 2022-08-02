package bin.study.memo.controller.server;

import bin.study.memo.domain.Tag;
import bin.study.memo.dto.TagDto;
import bin.study.memo.error.TagError;
import bin.study.memo.service.server.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Profile("server")
public class TagRestController {

    @Autowired
    private TagService tagService;

    @GetMapping("/tags")
    public List<Tag> findAllTag(){
        return tagService.findAllTag();
    }


    @PostMapping(value = "/tags")
    public TagError postTags(TagDto tag){
        boolean is_tag_insert = tagService.insert(tag);
        return TagError.builder().success(is_tag_insert).build();
    }

    @DeleteMapping(value = "/tags/{tid}")
    public TagError deleteTags(@PathVariable String tid){
        boolean is_tag_delete = tagService.deleteTag(tid);
        return TagError.builder().success(is_tag_delete).build();
    }
}
