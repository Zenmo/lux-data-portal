import {FunctionComponent} from "react"
import {css} from "@emotion/react"

const userFooterStyle = css({
    padding: "1rem 1.5rem",
    borderTop: "1px solid #e5e7eb",
})

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

const userInfoStyle = css({
    display: "flex",
    flexDirection: "column",
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

export const UserFooter: FunctionComponent<{username: string, isAdmin?: boolean}> = ({username, isAdmin}) => (
    <div css={userFooterStyle}>
        <div css={userCardStyle}>
            <div css={monogramStyle}>{username.charAt(0)}</div>
            <div css={userInfoStyle}>
                <span css={userNameStyle}>{username}</span>
                <span css={userRoleStyle}>{isAdmin ? "Beheerder account" : "Gebruiker account"}</span>
            </div>
        </div>
    </div>
)
