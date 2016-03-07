package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.config.AppGlobal;
import org.lucifer.abchat.domain.User;
import org.lucifer.abchat.dto.AppGlobalDTO;
import org.lucifer.abchat.service.AdminService;
import org.lucifer.abchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserService userService;

    @Override
    public boolean logInAdmin(User user) {
        return userService.logInAdmin(user);
    }

    @Override
    public AppGlobalDTO getConfig() {
        return new AppGlobalDTO();
    }

    @Override
    public AppGlobalDTO setConfig(AppGlobalDTO dto) {
        AppGlobal.setAddBotAfter(dto.isAddBotAfter());
        AppGlobal.setBotProbability(dto.getBotProbability() / 100);
        AppGlobal.setPointsLoose(dto.getPointsLoose());
        AppGlobal.setPointsWin(dto.getPointsWin());
        AppGlobal.setRetries(dto.getRetries());
        return new AppGlobalDTO();
    }
}
