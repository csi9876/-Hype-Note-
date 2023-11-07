"use client";

import { useTheme } from "next-themes";
import { useEffect, useState } from "react";
import { BiSolidSun } from "react-icons/bi";
import { BsFillMoonFill, BsThreeDots } from "react-icons/bs";

export default function DarkModeBtn() {
  const [mounted, setMounted] = useState(false);
  const [currentTheme, setCurrentTheme] = useState<"light" | "dark" | null>(null);
  const { setTheme } = useTheme();

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (typeof window !== "undefined") {
      const localTheme = window.localStorage.getItem("theme");
      setCurrentTheme(localTheme === "light" ? "light" : "dark");
    }
  }, []);

  useEffect(() => {
    if (currentTheme) {
      setTheme(currentTheme);
      window.localStorage.setItem("theme", currentTheme);
    }
  }, [currentTheme]);

  const changeTheme = () => {
    setCurrentTheme((current) => (current === "dark" ? "light" : "dark"));
  };

  // 클라이언트 측에서만 버튼을 렌더링
  return mounted ? (
    <div>
      {currentTheme === "light" ? (
        <div>
          <button
            title="다크모드로 변경"
            onClick={changeTheme}
            className="hover:bg-font_primary hover:bg-opacity-30 justify-start items-center flex bg-transparent w-[45px] h-[25px] border-[1.6px] hover:bg-gray-300 text-secondary rounded-3xl">
            <BiSolidSun className="ml-1.5 text-[18px]" />
          </button>
        </div>
      ) : (
        <div>
          <button
            title="라이트모드로 변경"
            onClick={changeTheme}
            className="hover:bg-font_primary hover:bg-opacity-30 justify-end items-center flex bg-transparent w-[45px] h-[25px] border-[1.6px] hover:bg-gray-300 text-secondary rounded-3xl">
            <BsFillMoonFill className="mr-1.5 mb-[1px] text-[12px]" />
          </button>
        </div>
      )}
    </div>
  ) : (
    <div>
      <button
        title="다크모드로 변경"
        onClick={changeTheme}
        className="hover:bg-font_primary hover:bg-opacity-30 justify-center items-center flex bg-transparent w-[45px] h-[25px] border-[1.6px] hover:bg-gray-300 text-secondary rounded-3xl">
        <BsThreeDots className="text-[18px] rounded-xl" />
      </button>
    </div>
  );
}