import {FunctionComponent, useState} from "react"
import {Button} from "primereact/button"
import {Sidebar} from "primereact/sidebar"
import {css} from "@emotion/react"
import {useUser} from "../../user/use-user"
import {redirectToLogin} from "../../admin/user/use-users"
import {BsList} from "react-icons/bs"
import {SidebarHeader, ZENMO_LOGO} from "./sidebar-header"
import {SidebarNav} from "./sidebar-nav"
import {UserFooter} from "./user-footer"

const sidebarStyle = css({
    width: "17rem",
    backgroundColor: "#ffffff",
})

const hamburgerButtonStyle = css({
    borderRadius: "0.375rem",
    border: "1px solid #06b6d4",
    background: "#06b6d4",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: "2.5rem",
    height: "2.5rem",
    color: "#ffffff",
})

const navbarHeadingStyle = css({
    fontWeight: 700,
    color: "#1e3a5f",
    margin: 0,
})

export const ZeroHeader: FunctionComponent = () => {
    const {isLoggedIn, username, isAdmin} = useUser()
    const [visible, setVisible] = useState(false)

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
                    <button onClick={() => setVisible(true)} css={hamburgerButtonStyle}>
                        <BsList />
                    </button>
                    <h3 css={navbarHeadingStyle}>LUX Dataportaal</h3>
                </div>

                <div style={{display: "flex", alignItems: "center", gap: "0.5rem"}}>
                    {!isLoggedIn && (
                        <Button
                            label="Log In"
                            className="p-button-text"
                            onClick={redirectToLogin}
                            css={{fontSize: "0.9em", cursor: "pointer"}}
                        />
                    )}
                    <a href="https://zenmo.com">
                        <img
                            alt="Zenmo logo"
                            className={"d-inline-block"}
                            style={{height: "50px"}}
                            src={ZENMO_LOGO}
                        />
                    </a>
                </div>
            </div>

            <Sidebar
                visible={visible}
                position="left"
                onHide={() => setVisible(false)}
                header={<SidebarHeader onClose={() => setVisible(false)} />}
                pt={{
                    closeButton: {style: {display: "none"}},
                    content: {style: {padding: "0", display: "flex", flexDirection: "column", height: "100%"}},
                }}
                css={sidebarStyle}
            >
                <div css={{flexGrow: 1}}>
                    <SidebarNav isAdmin={isAdmin} onNavigate={() => setVisible(false)} />
                </div>
                {isLoggedIn && username && (
                    <UserFooter username={username} isAdmin={isAdmin} />
                )}
            </Sidebar>
        </div>
    )
}
