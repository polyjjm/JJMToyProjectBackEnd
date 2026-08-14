package com.example.demo.board;

import com.example.demo.common.CustomException;
import com.example.demo.common.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

// Minimal, deliberately small comment feature: flat list per post, no nested replies,
// no editing, no likes. Ownership is enforced against the JWT identity (email), never a
// client-supplied username, so a comment can't be posted or deleted "as" someone else.
@Service
public class boardCommentServiceImpl implements boardCommentService {

    private final boardCommentMapper boardCommentMapper;

    public boardCommentServiceImpl(boardCommentMapper boardCommentMapper) {
        this.boardCommentMapper = boardCommentMapper;
    }

    @Override
    public List<boardCommentDTO> list(Integer board_no) throws Exception {
        return boardCommentMapper.selectByBoardNo(board_no);
    }

    @Override
    public boardCommentDTO insert(boardCommentDTO dto) throws Exception {
        dto.setComment_userName(currentUserEmail());
        boardCommentMapper.insert(dto);
        return dto;
    }

    @Override
    public void delete(Integer comment_no) throws Exception {
        String owner = boardCommentMapper.selectAuthor(comment_no);
        if (owner == null) {
            // Already deleted (e.g. double-click) - treat as success rather than an error.
            return;
        }
        if (!owner.equals(currentUserEmail())) {
            throw new CustomException(ErrorCode.COMMENT_FORBIDDEN, ErrorCode.COMMENT_FORBIDDEN.getErrorCode());
        }
        boardCommentMapper.delete(comment_no);
    }

    // Reads the logged-in user's email out of the JWT that JwtTokenFilter already validated
    // and placed on the SecurityContext (subject claim - see JwtTokenProvider.createToken).
    // NOTE: /board/** is permitAll in securityConfig (the whole board feature has no
    // endpoint-level auth requirement), so this is the one place in the board feature that
    // enforces "must be logged in" - it does so by rejecting an anonymous/missing identity
    // here rather than via Spring Security's authorizeHttpRequests.
    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED, ErrorCode.LOGIN_REQUIRED.getErrorCode());
        }
        return auth.getName();
    }
}
