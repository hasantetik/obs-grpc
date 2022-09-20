package org.work.controller;

import com.google.protobuf.Descriptors;
import org.springframework.web.bind.annotation.*;
import org.work.service.AnyService;

import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class AnyController {

    private final AnyService anyService;

    public AnyController(AnyService anyService) {
        this.anyService = anyService;
    }

    @CrossOrigin
    @GetMapping("/{firstName}/{secondName}")
    public Map<Descriptors.FieldDescriptor, Object> getAuthor(@PathVariable("firstName") String firstName,@PathVariable("secondName") String secondName) {
        return anyService.registration(firstName,secondName);
    }
}
