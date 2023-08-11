import React from "react"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { RootState } from "../store/Store"
import styled from "styled-components"
import { SubHeader } from "./inc/SubHeader"

export const EmptyPage = function () {
  const navigate = useNavigate()

  const goBack = () => {
    navigate(-1) // 뒤로가기
  }
  return (
    <div className=" bg-white h-screen">
      <div className="pt-12">
        <div className="w-10/12 m-auto flex justify-between">
          <BackIcon onClick={goBack} />
          <div
            className="flex items-center opacity-70"
            onClick={() => {
              navigate("/main")
            }}
          >
            <HomeIcon className="mr-2" />
            <TextStyle>홈으로</TextStyle>
          </div>
        </div>
      </div>
      <div className="">
        <BackgroundImg />
      </div>
    </div>
  )
}
const BackgroundImg = styled.div`
  background-image: url("assets/errorPage.png");
  background-size: 540px;
  background-position-x: center;
  background-position-y: 60px;
  background-repeat: no-repeat;
  width: 100%;
  height: 100vh;
`
const TextStyle = styled.p`
  font-family: "pretendard";
  font-weight: 500;
  font-size: 20px;
  color: ${(props) => props.theme.colorCommon};
  opacity: 80%;
`
const BackIcon = styled.div`
  background-image: url(${(props) =>
    props.theme.colorCommon === "black"
      ? "..//assets/icons/arrow_lD.png"
      : "..//assets/icons/arrow_l.png"});
  background-repeat: no-repeat;
  background-size: contain;
  width: 15px;
  height: 25px;
`
const HomeIcon = styled.div`
  background-image: url(${(props) =>
    props.theme.colorCommon === "black"
      ? "..//assets/icons/homeD.png"
      : "..//assets/icons/home.png"});
  background-repeat: no-repeat;
  background-size: contain;
  width: 25px;
  height: 25px;
`
