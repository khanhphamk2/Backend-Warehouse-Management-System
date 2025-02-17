package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.request.LoginRequest;
import org.khanhpham.wms.domain.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
