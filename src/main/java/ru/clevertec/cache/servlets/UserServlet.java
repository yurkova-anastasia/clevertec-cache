package ru.clevertec.cache.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import ru.clevertec.cache.config.AppConfig;
import ru.clevertec.cache.dto.UserRequestDto;
import ru.clevertec.cache.dto.UserResponseDto;
import ru.clevertec.cache.exception.BadRequestException;
import ru.clevertec.cache.service.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/users/*")
@Setter
public class UserServlet extends HttpServlet {

    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        userService = AppConfig.getUserService();
        objectMapper = AppConfig.getObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        String[] uriElements = req.getRequestURI().split("/");
        if (uriElements.length == 3) {
            validateRequestURIWithId(req);
            Long id = Long.valueOf(uriElements[2]);
            UserResponseDto userResponseDto = userService.findById(id);


            String result = objectMapper.writeValueAsString(userResponseDto);
            writer.print(result);
        } else if (uriElements.length == 2) {
            String size = req.getParameter("size");
            String page = req.getParameter("page");
            int limit = size == null ? 20 : Integer.parseInt(size);
            int offset = page == null ? 0 : Integer.parseInt(page);
            List<UserResponseDto> userResponseDtos = userService.findAll(limit, offset);
            String result = objectMapper.writeValueAsString(userResponseDtos);
            writer.print(result);
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] uriElements = req.getRequestURI().split("/");
        if (uriElements.length == 2) {
            UserRequestDto userRequestDto;
            try {
                userRequestDto = objectMapper.readValue(req.getInputStream(), UserRequestDto.class);
            } catch (IOException e) {
                throw new BadRequestException("Invalid request body content");
            }
            var userResponseDto = userService.save(userRequestDto);
            resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/" + userResponseDto.id());
        } else {
            throw new BadRequestException("Invalid HTTP request format");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        validateRequestURIWithId(req);
        String[] uriElements = req.getRequestURI().split("/");
        String id = uriElements[2];
        UserRequestDto userRequestDto;
        try {
            userRequestDto = objectMapper.readValue(req.getInputStream(), UserRequestDto.class);
        } catch (IOException e) {
            throw new BadRequestException("Invalid request body content");
        }
        boolean result = userService.updateById(Long.parseLong(id), userRequestDto);
        resp.getWriter().print(result);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        validateRequestURIWithId(req);
        String[] uriElements = req.getRequestURI().split("/");
        boolean result = userService.deleteById(Long.parseLong(uriElements[2]));
        resp.setStatus(204);
        writer.print(result);
        writer.close();
    }

    /**
     * Validates the HTTP request to ensure it follows the expected format.
     *
     * @param request The HttpServletRequest object representing the HTTP request.
     * @throws BadRequestException If the HTTP request format is invalid or the account number doesn't match the expected pattern.
     */
    private void validateRequestURIWithId(HttpServletRequest request) {
        String[] parts = request.getRequestURI().split("/");
        if (!(parts.length == 3 && parts[2].matches("^[1-9][0-9]*$"))) {
            throw new BadRequestException("Only entity plural name and its ID must be set in this request");
        }
    }
}
