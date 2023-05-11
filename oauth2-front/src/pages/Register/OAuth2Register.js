import React, { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useMutation } from 'react-query';
import axios from 'axios';

const OAuth2Register = () => {
    const [ passwords, setPasswords ] = useState({password: "", checkPassword: ""});
    const oauth2Register = useMutation(async (registerData) => {
        const option = {
            headers: {
                registerToken: `Bearer ${registerToken}`
            }
        }
        try{
            const response = await axios.post("http://localhost:8080/auth/oauth2/register", registerData, option);    // 객체로 보내면 자동으로 JSON으로 변환이 됨
            return response;
        }catch(error) {
            alert("페이지가 만료되었습니다.");
            window.location.replace("/auth/login"); // 뒤로가기 history가 안 남는다. 부분 재렌더링이 아니라 전체 페이지 재렌더링이기 때문에 상황에 맞게 잘 써야 한다.
            return error;
        }
    }, {
        onSuccess: (response) => {
            if(response.status === 200) {       // if를 해주지 않으면 에러가 떠도 아래 코드가 실행이 됨
                alert("회원가입 완료");
                window.location.replace("/auth/login")
            }
        }
    });
    const [ searchParams, setSearchParams ] = useSearchParams();

    const registerToken = searchParams.get("registerToken");
    const email = searchParams.get("email");
    const name = searchParams.get("name");
    const provider = searchParams.get("provider");

    const passwordInputChangeHandle = (e) => {
        const { name, value } = e.target;
        setPasswords({...passwords, [name]: value});
    }

    const oauth2RegisterSubmitHandle = () => {
        oauth2Register.mutate({
            email,
            name,
            provider,
            ...passwords
        })
    }

    return (
        <div>
            <input type="text" value={email} disabled={true}/>
            <input type="text" value={name} disabled={true}/>
            <input type="password" name="password" placeholder='비밀번호' onChange={passwordInputChangeHandle} />
            <input type="password" name="checkPassword" placeholder='비밀번호확인' onChange={passwordInputChangeHandle} />
            <button onClick={oauth2RegisterSubmitHandle}>가입하기</button>
        </div>
    );
};

export default OAuth2Register;