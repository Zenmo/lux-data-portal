import {FunctionComponent, ReactNode,PropsWithChildren, useState} from "react"
import {Button} from "primereact/button"
import {Sidebar} from "primereact/sidebar"
import {css} from "@emotion/react"
import {Link, useLocation} from "react-router-dom"
import {useUser} from "../user/use-user"
import {redirectToLogin} from "../admin/user/use-users"
import {BsBarChartLine, BsBriefcase, BsFileEarmarkText, BsHouseDoor, BsList, BsPeople, BsX} from "react-icons/bs"


const sidebarStyle = css({
    width: "17rem",
    backgroundColor: "#ffffff",
})

const sidebarHeaderStyle = css({
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    width: "100%",
    padding: "0.25rem 0",
})


const closeButtonStyle = css({
    border: "1px solid #d1d5db",
    borderRadius: "0.375rem",
    background: "#f3f4f6",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: "2rem",
    height: "2rem",
    color: "#374151",
    "&:hover": {
        backgroundColor: "#e5e7eb",
    },
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

const navSectionLabelStyle = css({
    fontSize: "0.65rem",
    fontWeight: 700,
    letterSpacing: "0.12em",
    textTransform: "uppercase",
    color: "#9ca3af",
    padding: "0 1.5rem",
    marginBottom: "0.5rem",
})

const navListStyle = css({
    display: "flex",
    flexDirection: "column",
    gap: "0.15rem",
    padding: "1rem 1.5rem",
})

const navItemStyle = (active: boolean) => css({
    display: "flex",
    alignItems: "center",
    gap: "0.75rem",
    padding: "0.55rem 0.75rem",
    borderRadius: "0.5rem",
    color: active ? "#1e3a5f" : "#374151",
    backgroundColor: active ? "#eef2f9" : "transparent",
    fontWeight: active ? 700 : 400,
    fontSize: "0.925rem",
    textDecoration: "none",
    transition: "background-color 0.15s ease-in-out",
    "&:hover": {
        backgroundColor: active ? "#eef2f9" : "#f3f4f6",
        color: active ? "#1e3a5f" : "#111827",
    },
})

const navIconStyle = (active: boolean) => css({
    fontSize: "1.1rem",
    flexShrink: 0,
    opacity: active ? 1 : 0.75,
    color: active ? "#1e3a5f" : "#374151",
})

type NavItem = {
    to: string
    label: string
    icon: ReactNode
    adminOnly?: boolean
}


const userCardStyle = css({
    display: "flex",
    alignItems: "center",
    gap: "0.75rem",
    padding: "0.6rem 0.75rem",
    borderRadius: "0.5rem",
    backgroundColor: "#f3f4f6",
})

const monogramStyle = css({
    width: "2rem",
    height: "2rem",
    borderRadius: "50%",
    backgroundColor: "#d1d5db",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: 700,
    fontSize: "0.85rem",
    color: "#374151",
    flexShrink: 0,
    textTransform: "uppercase",
})

const userNameStyle = css({
    fontWeight: 600,
    fontSize: "0.875rem",
    color: "#111827",
})

const userRoleStyle = css({
    fontSize: "0.75rem",
    color: "#6b7280",
    lineHeight: 1.3,
})

const ZENMO_LOGO = "https://zenmo.com/wp-content/uploads/2018/12/zenmo-logo-website-grey.png"
const navItems: NavItem[] = [
    {to: "/", label: "Home", icon: <BsHouseDoor />},
    {to: "/surveys", label: "Surveys", icon: <BsFileEarmarkText />},
    {to: "/projects", label: "Projects", icon: <BsBriefcase />},
    {to: "/users", label: "Users", icon: <BsPeople />, adminOnly: true},
    {to: "/simulation", label: "Simulation", icon: <BsBarChartLine />},
]

const SidebarHeader: FunctionComponent<{ onClose: () => void }> = ({onClose}) => (
    <div css={sidebarHeaderStyle}>
        <img
            src={ZENMO_LOGO}
            alt="Zenmo logo"
            css={{height: "2.5rem"}}
        />
        <button css={closeButtonStyle} onClick={onClose} aria-label="Sluiten">
            <BsX />
        </button>
    </div>
)

const userFooterStyle = css({
    padding: "1rem 1.5rem",
    borderTop: "1px solid #e5e7eb",
})

const UserFooter: FunctionComponent<{ username: string, isAdmin?: boolean }> = ({username, isAdmin}) => (
    <div css={userFooterStyle}>
        <div css={userCardStyle}>
            <div css={monogramStyle}>{username.charAt(0)}</div>
            <div css={{display: "flex", flexDirection: "column"}}>
                <span css={userNameStyle}>{username}</span>
                <span css={userRoleStyle}>{isAdmin ? "Beheerder account" : "Gebruiker account"}</span>
            </div>
        </div>
    </div>
)

export const ZeroHeader:FunctionComponent<PropsWithChildren & {}> = () => {
    const {isLoggedIn, username, isAdmin} = useUser()

    const [visible, setVisible] = useState(false)
    const location = useLocation()

    const isActive = (path: string) =>
        path === "/" ? location.pathname === "/" : location.pathname.startsWith(path)

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
                    <span css={navSectionLabelStyle}>Navigatie</span>
                    <hr css={{borderColor: "#e5e7eb", margin: "0.5rem 0 0.75rem"}} />
                    <nav css={navListStyle}>
                        {navItems
                            .filter(item => !item.adminOnly || isAdmin)
                            .map(item => (
                                <Link
                                    key={item.to}
                                    to={item.to}
                                    css={navItemStyle(isActive(item.to))}
                                    onClick={() => setVisible(false)}
                                >
                                    <span css={navIconStyle(isActive(item.to))}>{item.icon}</span>
                                    {item.label}
                                </Link>
                            ))
                        }
                    </nav>
                </div>
                {isLoggedIn && username && (
                    <UserFooter username={username} isAdmin={isAdmin} />
                )}
            </Sidebar>
        </div>
    )
}