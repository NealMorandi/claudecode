package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.RoomDTO;
import com.dom.dormitory.entity.vo.RoomVO;
import com.dom.dormitory.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 部屋コントローラ */
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /** 寮別部屋一覧取得 */
    @GetMapping("/dormitories/{dormitoryId}/rooms")
    public Result<List<RoomVO>> listByDormitory(@PathVariable Long dormitoryId) {
        return Result.success(roomService.listByDormitory(dormitoryId), MessageConstant.GET_SUCCESS);
    }

    /** 部屋新規登録 */
    @PostMapping("/dormitories/{dormitoryId}/rooms")
    public Result<RoomVO> create(@PathVariable Long dormitoryId, @RequestBody RoomDTO dto) {
        return Result.success(roomService.create(dormitoryId, dto), MessageConstant.CREATE_SUCCESS);
    }

    /** 部屋詳細取得 */
    @GetMapping("/rooms/{id}")
    public Result<RoomVO> getById(@PathVariable Long id) {
        return Result.success(roomService.getById(id), MessageConstant.GET_SUCCESS);
    }

    /** 部屋更新 */
    @PutMapping("/rooms/{id}")
    public Result<RoomVO> update(@PathVariable Long id, @RequestBody RoomDTO dto) {
        return Result.success(roomService.update(id, dto), MessageConstant.UPDATE_SUCCESS);
    }

    /** 部屋削除 */
    @DeleteMapping("/rooms/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.success(null, MessageConstant.DELETE_SUCCESS);
    }

    /** 空室一覧取得 */
    @GetMapping("/rooms/available")
    public Result<List<RoomVO>> listAvailable(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String gender) {
        return Result.success(roomService.listAvailable(date, gender), MessageConstant.GET_SUCCESS);
    }

    /** 全部屋一覧取得（備品登録用） */
    @GetMapping("/rooms")
    public Result<List<RoomVO>> listAll() {
        return Result.success(roomService.listAll(), MessageConstant.GET_SUCCESS);
    }
}
