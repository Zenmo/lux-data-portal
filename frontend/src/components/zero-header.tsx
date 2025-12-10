import React, {FunctionComponent, PropsWithChildren, useState} from "react"
import {Button} from "primereact/button"
import {Sidebar} from "primereact/sidebar"
import {css} from "@emotion/react"
import {To, useNavigate} from "react-router-dom"
import {useUser} from "../user/use-user"
import {redirectToLogin} from "../admin/user/use-users"
import {FaHouse} from "react-icons/fa6"
import {FaBusinessTime, FaChartLine, FaFileContract, FaUsers} from "react-icons/fa"

const sidebarStyle = css({
    width: "16rem",
    backgroundColor: "#f5f5f5",
    borderRight: "1px solid #ddd",
})

const buttonStyle = css({
    display: "block",
    marginBottom: "3rem",
    width: "100%",

    textAlign: "left",
    padding: "0.5em 1em",
    border: "none",
    borderBottom: "1px solid #ddd",
    color: "#333",
    background: "#f5f5f5",
    transition: "background-color 0.2s ease-in-out",
    fontWeight: "normal",
    cursor: "pointer",
    textDecoration: "none",
    "&:hover": {
        backgroundColor: "#ebebeb",
        color: "#007ad9",
    },
})

export const ZeroHeader: FunctionComponent<PropsWithChildren & {}> = () => {
    const {isLoading, isLoggedIn, username, isAdmin} = useUser()

    const [visible, setVisible] = useState(false)
    const navigate = useNavigate()

    const loadContent = (navidateTo: To) => {
        setVisible(false)
        navigate(navidateTo)
    }
    return (
        <div className="app-header">

            <div className="header" css={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                padding: "1em 1em",
                backgroundColor: "#f5f5f5",
                boxShadow: "1px solid #ddd",
            }}>
                <div className={"d-flex flex-row gap-3 align-items-center"}>
                    <Button icon="pi pi-bars" onClick={() => setVisible(true)} className={"rounded rounded-3"} />
                    <span className={" fs-2 fw-bold"}>
                    Zenmo Zero
                </span>
                </div>

                <div style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                }}>
                    {!isLoggedIn && (
                        <Button
                            label="Log In"
                            className="p-button-text"
                            onClick={redirectToLogin}
                            css={{marginLeft: "auto", fontSize: "0.9em", cursor: "pointer"}}
                        />
                    )}
                    <a href="https://zenmo.com">
                        <img
                            alt="Zenmo logo"
                            className={"d-inline-block"}
                            style={{height: "50px"}}
                            src="https://zenmo.com/wp-content/uploads/2018/12/zenmo-logo-website-grey.png"
                        />
                    </a>
                </div>
            </div>

            <Sidebar visible={visible} position="left" onHide={() => setVisible(false)} css={sidebarStyle}>
                <a onClick={() => loadContent(`/`)} css={buttonStyle}>
                    <i className="fas fa-house me-2"></i>
                    <FaHouse className={"me-2"} />
                    Home
                </a>
                <a onClick={() => loadContent("/surveys")} css={buttonStyle}>
                    <FaFileContract className={"me-2"} />
                    Surveys
                </a>
                <a onClick={() => loadContent("/projects")} css={buttonStyle}>
                    <FaBusinessTime className={"me-2"} />
                    Projects
                </a>
                {isAdmin && (
                    <a onClick={() => loadContent("/users")} css={buttonStyle}>
                        <FaUsers className={"me-2"} />
                        Users
                    </a>
                )}
                <a onClick={() => loadContent("/simulation")} css={buttonStyle}>
                    <FaChartLine className={"me-2"} />
                    Simulation
                </a>
            </Sidebar>
        </div>
    )
}
