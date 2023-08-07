import React, { useEffect } from "react"
import axios from "axios"
import { useNavigate } from "react-router-dom"
import { useDispatch, useSelector } from "react-redux"
import { getCookieToken, removeCookieToken } from "../../store/Cookie"
import { DELETE_TOKEN, DELETE_USER } from "../../store/Auth"
import { serverUrl } from "../../urls"
import { changeUniverseDarkTheme } from "../../store/Theme"
import { DELETE_TIMECAPSULE } from "../../store/Timecapsule"
import { EventSourcePolyfill } from "event-source-polyfill"
import { RootState } from "../../store/Store"

export const Logout = function () {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const userdata = useSelector((state: RootState) => state.auth.userInfo)
  const token = useSelector((state: RootState) => state.auth.accessToken)

  useEffect(() => {
    let eventSource: EventSource
    const UnSse = () => {
      try {
        eventSource = new EventSourcePolyfill(
          serverUrl + "sse/logout?userNo=" + userdata.userNo,
          {
            headers: {
              token: "Bearer " + token,
            },
          }
        )

        eventSource.onmessage = (event) => {
          console.log(event)
        }

        eventSource.onerror = (event) => {
          console.log(event)
          eventSource.close()
        }
      } catch (error) {}
    }
    UnSse()
    axios({
      method: "POST",
      url: serverUrl + "user/logout",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + getCookieToken(),
      },
      data: {},
    })
      .then(() => {
        dispatch(DELETE_TOKEN())
        dispatch(DELETE_USER())
        dispatch(changeUniverseDarkTheme())
        dispatch(DELETE_TIMECAPSULE())
        removeCookieToken()
        navigate("/login")
      })
      .catch((error) => console.error(error))
  }, [])

  return <div></div>
}
