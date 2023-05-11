import { atom } from "recoil";

export const authenticationState = atom({   // 로그인 상태를 저장
    key: "authenticationState",
    default: false
});