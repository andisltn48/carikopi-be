package com.andi.carikopi.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MenuFotoRequest {

    private List<MultipartFile> foto;
}
