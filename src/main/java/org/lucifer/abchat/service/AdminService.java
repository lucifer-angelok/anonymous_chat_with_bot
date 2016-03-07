package org.lucifer.abchat.service;


import org.lucifer.abchat.domain.User;
import org.lucifer.abchat.dto.AppGlobalDTO;

public interface AdminService {
    boolean logInAdmin(User user);

    AppGlobalDTO getConfig();

    AppGlobalDTO setConfig(AppGlobalDTO dto);
}
