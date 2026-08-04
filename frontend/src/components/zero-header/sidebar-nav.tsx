import {FunctionComponent} from "react"
import {Link, useLocation} from "react-router-dom"
import {css} from "@emotion/react"
import {navItems} from "./nav-items"

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

export const SidebarNav: FunctionComponent<{isAdmin?: boolean, onNavigate: () => void}> = ({isAdmin, onNavigate}) => {
    const location = useLocation()

    const isActive = (path: string) =>
        path === "/" ? location.pathname === "/" : location.pathname.startsWith(path)

    return (
        <div>
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
                            onClick={onNavigate}
                        >
                            <span css={navIconStyle(isActive(item.to))}>{item.icon}</span>
                            {item.label}
                        </Link>
                    ))
                }
            </nav>
        </div>
    )
}
