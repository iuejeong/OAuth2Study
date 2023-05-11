import axios from 'axios';
import React from 'react';
import { useQuery } from 'react-query';
import { useRecoilState } from 'recoil';
import { authenticationState } from '../../store/atoms/AuthAtoms';
import { useNavigate } from 'react-router-dom';

const AuthRoute = ({ path, element }) => {
    const navigate = useNavigate();
    const [ authState, setAuthState ] = useRecoilState(authenticationState);

    // 로그아웃은 전역상태를 false로 주고, localStorage를 삭제한다.
    
    const authenticated = useQuery(["authenticated"], async () => {
        const option = {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            }
        }
        return await axios.get("http://localhost:8080/auth/authenticated", option)
    }, {
        onSuccess: (response) => {
            if(response.status === 200) {
                if(response.data) {
                    setAuthState(true);     // 로그인된 걸로 간주
                }
            }
        }
    });

    const authenticatedPaths = ["/mypage", "/user"];
    const authPath = "/auth"

    if(authenticated.isLoading) {
        return <></>
    }

    if(authState && path.startsWith(authPath)) { // auth로 시작하는 요청이 들어오고, 인증이 되어있으면 메인으로.
        navigate("/");
    }

        // ex) /mypage로 시작하는지 확인. 그리고 확인이 되면 그것을 배열에 담는다. 
    if(!authState && authenticatedPaths.filter(authenticatedPath => path.startsWith(authenticatedPath)).length > 0) {
        navigate("/auth/login");
    }
    return element
};

export default AuthRoute;