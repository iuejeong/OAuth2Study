import React, { useState } from 'react';
import { FcGoogle } from 'react-icons/fc';

const Login = () => {

    const googleAuthLoginClickHandle = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    }

    return (
        <div>
            <input type="text" placeholder='email'/>
            <input type="password" placeholder='password'/>
            <button>로그인zz</button>
            <button onClick={googleAuthLoginClickHandle}><FcGoogle /></button>
        </div>
    );
};

export default Login;