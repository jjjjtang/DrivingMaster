package com.tzc.drivemasterserver.service;

import com.tzc.drivemasterserver.mapper.LogMapper;
import com.tzc.drivemasterserver.model.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    private final LogMapper logMapper;

    public LogService(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    /**
     * 记录日志（推荐用这个）
     */
    public void addLog(String content, Integer userId, String username) {
        Log log = new Log();
        log.setContent(content);
        log.setUserId(userId);
        log.setUsername(username);
        log.setCreateTime(LocalDateTime.now());

        logMapper.insert(log);
    }

    /**
     * 直接传 Log 对象保存
     */
    public void addLog(Log log) {
        if (log.getCreateTime() == null) {
            log.setCreateTime(LocalDateTime.now());
        }
        logMapper.insert(log);
    }

    /**
     * 根据 ID 查询
     */
    public Log getById(Long logId) {
        return logMapper.selectById(logId);
    }

    /**
     * 查询所有日志
     */
    public List<Log> listAll() {
        return logMapper.selectAll();
    }

    /**
     * 删除日志
     */
    public boolean delete(Long logId) {
        return logMapper.deleteById(logId) > 0;
    }
}